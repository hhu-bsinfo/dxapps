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

import java.util.Collections;
import java.util.List;

import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxterm.TerminalCommandString;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * List available dxapplications to run.
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 01.10.2018
 */
public class TcmdAppList extends AbstractTerminalCommand {
    public TcmdAppList() {
        super("applist");
    }

    @Override
    public String getHelp() {
        return "Lists available applications to run on a remote peer\n" + "Usage: applist [nid]\n" +
                "  nid: NID of the remote peer to get the application list of";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout,
            final TerminalServerStdin p_stdin, final TerminalServiceAccessor p_services) {
        short nid = p_cmd.getArgument(0, NodeID::parse, NodeID.INVALID_ID);

        if (nid != NodeID.INVALID_ID) {
            // TODO must support remote listing on service first
            p_stdout.printlnErr("Operation not supported");
        }

        ApplicationService appService = p_services.getService(ApplicationService.class);

        StringBuilder builder = new StringBuilder();

        List<String> apps = appService.getLoadedApplicationClasses();

        builder.append("Available applications (").append(apps.size()).append("):\n");

        for (String clazz : apps) {
            builder.append(clazz).append('\n');
        }

        p_stdout.print(builder.toString());
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
