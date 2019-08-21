package de.hhu.bsinfo.dxapp.tasks;

import de.hhu.bsinfo.dxram.ms.Signal;
import de.hhu.bsinfo.dxram.ms.Task;
import de.hhu.bsinfo.dxram.ms.TaskContext;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import de.hhu.bsinfo.dxapp.chunks.RootChunk;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;


public class InitTask implements Task {

    public InitTask() { }

    @Override
    public int execute(TaskContext taskContext) {
        BootService bootService = taskContext.getDXRAMServiceAccessor().getService(BootService.class);
        ChunkService chunkService = taskContext.getDXRAMServiceAccessor().getService(ChunkService.class);

        System.out.printf("  DxddlDemoApplication: slave execute called.\n");
        short myNodeID = taskContext.getCtxData().getOwnNodeId();
        short mySlaveIndex = taskContext.getCtxData().getSlaveId();

        // create metadata chunk
        RootChunk vc = new RootChunk( mySlaveIndex );
        chunkService.create().create( myNodeID, vc);
        chunkService.put().put( vc );

        // register metadata chunk in nameservice, name will be myNodeID
        NameserviceService nameService = taskContext.getDXRAMServiceAccessor().getService( NameserviceService.class );
        // we cannot simply convert the NodeID to a string as it might be negative and then the name would be too long
        String nodeIDstr = Integer.toHexString(0xFFFF & myNodeID);
        System.out.printf("  DxddlDemoApplication: slave registering nid=%d as str=%s.\n", myNodeID, nodeIDstr);
        nameService.register(vc, nodeIDstr);


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
