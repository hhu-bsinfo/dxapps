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
import de.hhu.bsinfo.dxram.util.NodeCapabilities;
import de.hhu.bsinfo.dxterm.TerminalCommandString;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Collections;
import java.util.List;

/**
 * Get information about either the current node or another node in the network
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdNodeinfo extends AbstractTerminalCommand {
    public TcmdNodeinfo() {
        super("nodeinfo");
    }

    @Override
    public String getHelp() {
        return "Get information about either the current node or another node in the network\n\n" +
                "Usage (1): nodeinfo [nid]\n" +
                "\t nid \t If specified, gets information of this node";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {

        BootService boot = p_services.getService(BootService.class);

        short nid = p_cmd.getArgument(0, NodeID::parse, boot.getNodeID());

        if (boot.isNodeOnline(nid)) {
            p_stdout.printfln("Node info 0x%X:", nid);
            p_stdout.printfln("\tRole: %s", boot.getNodeRole(nid));
            p_stdout.printfln("\tAddress: %s", boot.getNodeAddress(nid));
            p_stdout.printfln("\tCapabilities: %s", NodeCapabilities.toString(boot.getNodeCapabilities(nid)));
        } else {
            p_stdout.printfln("Not available.");
        }
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final de.hhu.bsinfo.dxterm.TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        switch (p_argumentPos) {
            case 0:
                return TcmdUtils.getAllOnlineNodeIDsCompSuggestions(p_services);
            default:
                return Collections.emptyList();
        }
    }
}
