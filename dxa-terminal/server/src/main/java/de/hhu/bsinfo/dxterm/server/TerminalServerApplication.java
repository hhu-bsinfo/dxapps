/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxterm.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkAnonService;
import de.hhu.bsinfo.dxram.chunk.ChunkDebugService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.AbstractDXRAMService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.log.LogService;
import de.hhu.bsinfo.dxram.logger.LoggerService;
import de.hhu.bsinfo.dxram.lookup.LookupService;
import de.hhu.bsinfo.dxram.migration.MigrationService;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.stats.StatisticsService;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;
import de.hhu.bsinfo.dxram.tmp.TemporaryStorageService;
import de.hhu.bsinfo.dxterm.TerminalException;
import de.hhu.bsinfo.dxterm.TerminalSession;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdBarrieralloc;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdBarrierfree;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdBarriersignon;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdBarriersize;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdBarrierstatus;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkMigrate;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkcreate;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkdump;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkget;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunklist;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkput;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkremove;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkremoverange;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdChunkstatus;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdCompgrpls;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdCompgrpstatus;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdComptask;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdComptaskscript;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdLoggerlevel;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdLoginfo;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdLookuptree;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdMemdump;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdMetadata;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNameget;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNamelist;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNamereg;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNodeinfo;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNodelist;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNodeshutdown;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdNodewait;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdStartApp;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdStatsprint;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdTmpcreate;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdTmpget;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdTmpput;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdTmpremove;
import de.hhu.bsinfo.dxterm.server.cmd.TcmdTmpstatus;

/**
 * Terminal server running on a DXRAM peer as a DXRAM application. Thin clients can connect to the server and execute
 * terminal commands on the peer
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 24.05.2017
 */
public class TerminalServerApplication extends AbstractApplication implements TerminalSession.Listener,
        TerminalServiceAccessor {
    private static final Logger LOGGER = LogManager.getFormatterLogger(TerminalServerApplication.class.getSimpleName());

    private TerminalServer m_terminalServer;

    private ServerSocket m_socket;
    private ExecutorService m_threadPool;

    private volatile boolean m_run = true;
    private List<de.hhu.bsinfo.dxterm.TerminalSession> m_sessions = Collections.synchronizedList(new ArrayList<>());

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "TerminalServer";
    }

    @Override
    public void main(final String[] p_args) {
        if (p_args.length < 2) {
            LOGGER.error("Insufficient arguments: <port> <max sessions>");
            return;
        }

        int port = Integer.parseInt(p_args[0]);
        int maxSessions = Integer.parseInt(p_args[1]);

        short nodeId = getService(BootService.class).getNodeID();

        m_terminalServer = new TerminalServer(nodeId);
        registerTerminalCommands();

        m_threadPool = Executors.newFixedThreadPool(maxSessions);

        try {
            m_socket = new ServerSocket(port);
            m_socket.setSoTimeout(1000);
        } catch (final IOException e) {
            LOGGER.error("Creating server socket failed", e);
            return;
        }

        LOGGER.info("Started server on port %d, max sessions %d", port, maxSessions);

        while (m_run) {
            if (m_sessions.size() == maxSessions) {
                try {
                    Thread.sleep(1000);
                } catch (final InterruptedException ignored) {

                }

                continue;
            }

            Socket sock;

            try {
                sock = m_socket.accept();
            } catch (final SocketTimeoutException ignored) {
                // accept timeout, just continue
                continue;
            } catch (final IOException e) {
                LOGGER.error("Accepting client connection failed", e);
                continue;
            }

            LOGGER.debug("Accepted connection: %s", sock);

            de.hhu.bsinfo.dxterm.TerminalSession session;

            try {
                session = new TerminalSession((byte) m_sessions.size(), sock, this);
            } catch (final TerminalException e) {
                LOGGER.error("Creating terminal session failed", e);

                try {
                    sock.close();
                } catch (final IOException ignored) {

                }

                continue;
            }

            LOGGER.info("Created terminal client session: %s", session);

            m_sessions.add(session);
            m_threadPool.submit(new TerminalServerSession(m_terminalServer, session, this));

            if (m_sessions.size() == maxSessions) {
                LOGGER.debug("Max session limit (%d) reached, further sessions won't be accepted", maxSessions);
            }
        }
    }

    @Override
    public void signalShutdown() {
        m_run = false;
    }

    @Override
    public void sessionClosed(final TerminalSession p_session) {
        m_sessions.remove(p_session);
    }

    @Override
    public <T extends AbstractDXRAMService> T getService(final Class<T> p_class) {
        return super.getService(p_class);
    }

    /**
     * Register all available terminal commands
     */
    private void registerTerminalCommands() {
        if (isServiceAvailable(BootService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdNodeinfo());
            m_terminalServer.registerTerminalCommand(new TcmdNodelist());
            m_terminalServer.registerTerminalCommand(new TcmdNodeshutdown());
            m_terminalServer.registerTerminalCommand(new TcmdNodewait());
        }

        if (isServiceAvailable(ChunkAnonService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdChunkget());
            m_terminalServer.registerTerminalCommand(new TcmdChunkput());
        }

        if (isServiceAvailable(ChunkDebugService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdMemdump());
        }

        if (isServiceAvailable(ChunkService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdChunkcreate());
            m_terminalServer.registerTerminalCommand(new TcmdChunkdump());
            m_terminalServer.registerTerminalCommand(new TcmdChunklist());
            m_terminalServer.registerTerminalCommand(new TcmdChunkstatus());
            m_terminalServer.registerTerminalCommand(new TcmdChunkremove());
            m_terminalServer.registerTerminalCommand(new TcmdChunkremoverange());
        }

        // TODO
        //  Is locking still available?
        //        if (isServiceAvailable(AbstractLockService.class)) {
        //            m_terminalServer.registerTerminalCommand(new TcmdChunklock());
        //            m_terminalServer.registerTerminalCommand(new TcmdChunklocklist());
        //            m_terminalServer.registerTerminalCommand(new TcmdChunkunlock());
        //        }

        if (isServiceAvailable(LogService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdLoginfo());
        }

        if (isServiceAvailable(LoggerService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdLoggerlevel());
        }

        if (isServiceAvailable(LookupService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdLookuptree());
            m_terminalServer.registerTerminalCommand(new TcmdMetadata());
        }

        if (isServiceAvailable(MasterSlaveComputeService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdCompgrpls());
            m_terminalServer.registerTerminalCommand(new TcmdCompgrpstatus());
            m_terminalServer.registerTerminalCommand(new TcmdComptask());
            m_terminalServer.registerTerminalCommand(new TcmdComptaskscript());
        }

        if (isServiceAvailable(MigrationService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdChunkMigrate());
        }

        if (isServiceAvailable(NameserviceService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdNameget());
            m_terminalServer.registerTerminalCommand(new TcmdNamelist());
            m_terminalServer.registerTerminalCommand(new TcmdNamereg());
        }

        if (isServiceAvailable(StatisticsService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdStatsprint());
        }

        if (isServiceAvailable(SynchronizationService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdBarrieralloc());
            m_terminalServer.registerTerminalCommand(new TcmdBarrierfree());
            m_terminalServer.registerTerminalCommand(new TcmdBarriersignon());
            m_terminalServer.registerTerminalCommand(new TcmdBarriersize());
            m_terminalServer.registerTerminalCommand(new TcmdBarrierstatus());
        }

        if (isServiceAvailable(TemporaryStorageService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdTmpcreate());
            m_terminalServer.registerTerminalCommand(new TcmdTmpget());
            m_terminalServer.registerTerminalCommand(new TcmdTmpput());
            m_terminalServer.registerTerminalCommand(new TcmdTmpremove());
            m_terminalServer.registerTerminalCommand(new TcmdTmpstatus());
        }

        if (isServiceAvailable(ApplicationService.class)) {
            m_terminalServer.registerTerminalCommand(new TcmdStartApp());
        }
    }
}
