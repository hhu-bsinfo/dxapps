package de.hhu.bsinfo.dxapp.tasks;

import de.hhu.bsinfo.dxram.ms.Signal;
import de.hhu.bsinfo.dxram.ms.Task;
import de.hhu.bsinfo.dxram.ms.TaskContext;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;


public class GenerateDataTask implements Task {

    public GenerateDataTask() { }

    @Override
    public int execute(TaskContext taskContext) {
        System.out.printf("  DxddlDemoApplication: slave execute called.\n");
        return 0;
    }


    @Override
    public void handleSignal(Signal signal) {

    }

    @Override
    public void exportObject(Exporter exporter) { }

    @Override
    public void importObject(Importer importer) { }

    @Override
    public int sizeofObject() { return 0; }
}
