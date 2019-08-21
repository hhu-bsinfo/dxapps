package de.hhu.bsinfo.dxapp.tasks;

import de.hhu.bsinfo.dxram.ms.Signal;
import de.hhu.bsinfo.dxram.ms.Task;
import de.hhu.bsinfo.dxram.ms.TaskContext;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import de.hhu.bsinfo.dxapp.chunks.RootChunk;


public class InitTask implements Task {

    public GenerateDataTask() { }

    @Override
    public int execute(TaskContext taskContext) {
        System.out.printf("  DxddlDemoApplication: slave execute called.\n");
        short myNodeID = taskContext.getCtxData().getOwnNodeId();
        short mySlaveIndex = taskContext.getCtxData().getSlaveId();

        // create metadata chunk
        RootChunk vc = new RootChunk( mySlaveIndex );
        chunkService.create().create( myNodeID, vc);
        chunkService.put().put( vc );

        // register metadata chunk in nameservice, name will be myNodeID
        NameserviceService nameService = getService( NameserviceService.class );
        nameService.register(vc, new Short(myNodeID).toSting() );

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
