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

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxmem.data.ChunkByteArray;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxterm.*;

import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Collections;
import java.util.List;
import java.util.stream.LongStream;

/**
 * Remove a range of chunks
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdChunkremoverange extends AbstractTerminalCommand {
    public TcmdChunkremoverange() {
        super("chunkremoverange");
    }

    @Override
    public String getHelp() {
        return "Remove a range of existing chunks.\n" + "Usage: chunkremoverange <nid> <lid start> <lid end>\n" + "  nid: Node id of the range to remove\n" +
                "  lid start: Start lid of range (including)\n" + "  lid end: End lid of range (including)\n";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        short nid = p_cmd.getArgument(0, NodeID::parse, NodeID.INVALID_ID);
        long lidStart = p_cmd.getArgument(1, ChunkID::parse, ChunkID.INVALID_ID);
        long lidEnd = p_cmd.getArgument(2, ChunkID::parse, ChunkID.INVALID_ID);

        if (nid == NodeID.INVALID_ID) {
            p_stdout.printlnErr("None or invalid nid specified");
            return;
        }

        if (lidStart == NodeID.INVALID_ID) {
            p_stdout.printlnErr("None or invalid lid start specified");
            return;
        }

        if (lidEnd == NodeID.INVALID_ID) {
            p_stdout.printlnErr("None or invalid lid end specified");
            return;
        }

        if (lidEnd < lidStart) {
            p_stdout.printlnErr("Lid end < start");
            return;
        }

        // don't allow removal of index chunk
        if (lidStart == 0 || lidEnd == 0) {
            p_stdout.printlnErr("Removal of index chunk is not allowed");
            return;
        }

        ChunkService chunk = p_services.getService(ChunkService.class);

        long[] cids = LongStream.range(lidStart, lidEnd).toArray();

        if (chunk.remove().remove(cids) != cids.length - 1) {
            p_stdout.printflnErr("Removed chunk count did not match expected count");
            return;
        }

        p_stdout.printfln("Chunk(s) removed");
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        switch (p_argumentPos) {
            case 0:
                return TcmdUtils.getAllOnlinePeerNodeIDsCompSuggestions(p_services);
            default:
                return Collections.emptyList();
        }
    }
}
