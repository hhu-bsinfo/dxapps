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

/**
 * Starts an application.
 *
 * @author Filip Krakowski, Filip.Krakowski@Uni-Duesseldorf.de, 22.08.2018
 */
public class TcmdAppRun extends AbstractTerminalCommand {
    public TcmdAppRun() {
        super("apprun");
    }

    private static final String ARG_NAME = "name";

    @Override
    public String getHelp() {
        return "Starts an application\n" + "Usage: apprun <name> [args]\n" +
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

        String name = p_cmd.getArgument(0);

        ApplicationService appService = p_services.getService(ApplicationService.class);

        String[] args;

        if (argCount > 1) {
            args = Arrays.copyOfRange(p_cmd.getArgs(), 1, argCount);
        } else {
            args = new String[0];
        }

        if (!appService.startApplication(name, args)) {
            p_stdout.printlnErr("The application could not be started");
        }
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
