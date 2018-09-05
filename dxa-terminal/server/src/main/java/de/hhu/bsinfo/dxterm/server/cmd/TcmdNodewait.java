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

package de.hhu.bsinfo.dxterm.server.cmd;

import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxterm.*;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;

import java.util.Collections;
import java.util.List;

/**
 * Wait for a minimum number of nodes to be online
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdNodewait extends AbstractTerminalCommand {
    public TcmdNodewait() {
        super("nodewait");
    }

    @Override
    public String getHelp() {
        return "Wait for a minimum number of nodes to be online\n" + "Usage: nodewait [superpeers] [peers] [pollIntervalMs]\n" +
                "  superpeers: Number of available superpeers to wait for (default 0)\n" + "  peers: Number of available peers to wait for (default 0)\n" +
                "  pollIntervalMs: Polling interval when checking online status (default 1000)";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        int superpeers = p_cmd.getArgument(0, Integer::valueOf, 0);
        int peers = p_cmd.getArgument(1, Integer::valueOf, 0);
        int pollIntervalMs = p_cmd.getArgument(2, Integer::valueOf, 1000);

        BootService boot = p_services.getService(BootService.class);

        p_stdout.printfln("Waiting for at least %d superpeer(s) and %d peer(s)...", superpeers, peers);

        List<Short> listSuperpeers = boot.getOnlineSuperpeerNodeIDs();
        while (listSuperpeers.size() < superpeers) {
            try {
                Thread.sleep(pollIntervalMs);
            } catch (final InterruptedException ignored) {

            }

            listSuperpeers = boot.getOnlineSuperpeerNodeIDs();
        }

        List<Short> listPeers = boot.getOnlinePeerNodeIDs();
        while (listPeers.size() < peers) {
            try {
                Thread.sleep(pollIntervalMs);
            } catch (final InterruptedException ignored) {

            }

            listPeers = boot.getOnlinePeerNodeIDs();
        }

        p_stdout.printfln("%d superpeers and %d peers online", listSuperpeers.size(), listPeers.size());
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
