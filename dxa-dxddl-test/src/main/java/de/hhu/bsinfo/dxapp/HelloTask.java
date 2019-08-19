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

package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxram.ms.Task;
import de.hhu.bsinfo.dxram.ms.Signal;
import de.hhu.bsinfo.dxram.ms.TaskContext;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;

/**
 * Example for a task script to run with the MasterSlaveService of DXRAM.
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 18.02.2019
 */
public class HelloTask implements Task {
    /**
     * Constructor.
     */
    public HelloTask() {

    }

    @Override
    public int execute(final TaskContext p_ctx) {
        System.out.println("Hello from task running on slave " + p_ctx.getCtxData().getSlaveId());

        // Put your code running as a task on DXRAM peers here

        return 0;
    }

    @Override
    public void handleSignal(final Signal p_signal) {
        // ignore signals
    }

    @Override
    public void exportObject(final Exporter p_exporter) {
        // nothing to export
    }

    @Override
    public void importObject(final Importer p_importer) {
        // nothing to import
    }

    @Override
    public int sizeofObject() {
        return 0;
    }
}
