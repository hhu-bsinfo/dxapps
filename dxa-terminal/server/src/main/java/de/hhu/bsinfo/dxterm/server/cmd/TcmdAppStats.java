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

import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxterm.TerminalCommandString;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Starts an application.
 *
 * @author Filip Krakowski, Filip.Krakowski@Uni-Duesseldorf.de, 08.11.2018
 */
public class TcmdAppStats extends AbstractTerminalCommand {
    public TcmdAppStats() {
        super("appstats");
    }

    @Override
    public String getHelp() {
        return "Shows information about all running applications\n" + "Usage: appstats";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout,
            final TerminalServerStdin p_stdin, final TerminalServiceAccessor p_services) {

        ApplicationService appService = p_services.getService(ApplicationService.class);

        String table = appService.getRunningProcesses()
                .stream()
                .map(p_proc -> String.format("%04d\t%32s\t%32s\t%16s", p_proc.getId(), p_proc.getName(),
                        p_proc.getArguments(), String.valueOf(p_proc.getElapsedTime())))
                .collect(Collectors.joining("\n"));

        String header = String.format("%4s\t%32s\t%32s\t%16s", "id", "name", "arguments", "time");
        p_stdout.println(header);
        String seperator = header.chars().mapToObj(i -> "-").collect(Collectors.joining());
        p_stdout.println(seperator);
        p_stdout.print(table);
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
