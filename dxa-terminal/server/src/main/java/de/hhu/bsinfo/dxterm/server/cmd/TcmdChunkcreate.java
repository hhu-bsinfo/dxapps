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
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxterm.*;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Collections;
import java.util.List;

/**
 * Create a chunk on a remote node
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdChunkcreate extends AbstractTerminalCommand {
    public TcmdChunkcreate() {
        super("chunkcreate");
    }

    private static final String ARG_SIZE="size";
    private static final String ARG_NODEID="nid";

    @Override
    public String getHelp() {
        return "Create a chunk on a remote node\n" + "Usage: chunkcreate <size> <nid>\n" + "  size: Size of the chunk to create\n" +
                "  nid: Node id of the peer to create the chunk on";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        int size = p_cmd.getNamedArgument(ARG_SIZE, Integer::valueOf, -1);
        short nid = p_cmd.getNamedArgument(ARG_NODEID, NodeID::parse, NodeID.INVALID_ID);

        if (size == -1) {
            p_stdout.printlnErr("No size specified");
            return;
        }

        if (nid == NodeID.INVALID_ID) {
            p_stdout.printlnErr("No nid specified");
            return;
        }

        BootService boot = p_services.getService(BootService.class);
        ChunkService chunk = p_services.getService(ChunkService.class);
        ChunkLocalService chunkLocal = p_services.getService(ChunkLocalService.class);

        long[] chunkIDs = new long[1];
        int numChunks = 0;

        if (boot.getNodeID() == nid) {
            numChunks = chunkLocal.createLocal().create(chunkIDs, 1, size);
        } else {
            numChunks = chunk.create().create(nid, chunkIDs, 1, size);
        }

        if (numChunks != 1) {
            p_stdout.printlnErr("Chunk creation failed");
            return;
        }

        p_stdout.printfln("Created chunk of size %d: 0x%X", size, chunkIDs[0]);
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        if (p_argumentPos == 1) {
            return TcmdUtils.getAllOnlinePeerNodeIDsCompSuggestions(p_services);
        }

        return Collections.emptyList();
    }
}
