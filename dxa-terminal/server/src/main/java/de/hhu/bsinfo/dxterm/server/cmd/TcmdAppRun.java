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

import java.util.Arrays;
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
 * Starts an application.
 *
 * @author Filip Krakowski, Filip.Krakowski@Uni-Duesseldorf.de, 22.08.2018
 */
public class TcmdAppRun extends AbstractTerminalCommand {
    public TcmdAppRun() {
        super("apprun");
    }

    @Override
    public String getHelp() {
        return "Starts an application\n" + "Usage: apprun [nid] <name> [args]\n" +
                "  name: The application's name";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout,
            final TerminalServerStdin p_stdin, final TerminalServiceAccessor p_services) {
        int argCount = p_cmd.getArgc();

        if (argCount < 1) {
            p_stdout.printlnErr("No application name specified");
            return;
        }

        // reflect on first argument
        String arg1 = p_cmd.getArgument(0);

        short nid;
        String name;
        int appArgStartIdx;

        try {
            nid = p_cmd.getArgument(0, NodeID::parse, NodeID.INVALID_ID);
            name = p_cmd.getArgument(1);
            appArgStartIdx = 2;
        } catch (NumberFormatException e) {
            nid = NodeID.INVALID_ID;
            // not a nid, must be name
            name = arg1;
            appArgStartIdx = 1;
        }

        ApplicationService appService = p_services.getService(ApplicationService.class);

        String[] args;

        if (argCount > appArgStartIdx) {
            args = Arrays.copyOfRange(p_cmd.getArgs(), appArgStartIdx, argCount);
        } else {
            args = new String[0];
        }

        System.out.println(nid + " " + name + " " + appArgStartIdx);

        if (nid != NodeID.INVALID_ID) {
            if (!appService.startApplication(nid, name, args)) {
                p_stdout.printflnErr("Starting application on remote peer %s failed", NodeID.toHexString(nid));
            }
        } else {
            if (!appService.startApplication(name, args)) {
                p_stdout.printflnErr("Starting application failed");
            }
        }
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
