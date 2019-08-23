package de.hhu.bsinfo.dxapp.tasks;

import de.hhu.bsinfo.dxram.ms.Signal;
import de.hhu.bsinfo.dxram.ms.Task;
import de.hhu.bsinfo.dxram.ms.TaskContext;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxapp.chunks.RootChunk;
import de.hhu.bsinfo.dxapp.chunks.NodeChunk;


public class InitTask implements Task {

    private final static int ENTRIES = 10;     // number of entries in list

    public InitTask() { }

    @Override
    public int execute(TaskContext taskContext) {
        BootService bootService = taskContext.getDXRAMServiceAccessor().getService(BootService.class);
        ChunkService chunkService = taskContext.getDXRAMServiceAccessor().getService(ChunkService.class);

        short myNodeID = taskContext.getCtxData().getOwnNodeId();

        System.out.printf("  DxddlDemoApplication (slave): execute called.\n");

        //
        // create list
        //
        RootChunk rc = new RootChunk( ChunkID.INVALID_ID,0 );
        chunkService.create().create( myNodeID, rc);

        for (int i=0; i<ENTRIES; i++) {
            NodeChunk nc = new NodeChunk( myNodeID+i, rc.getRoot() );
            chunkService.create().create(myNodeID, nc);
            chunkService.put().put(nc);

            rc.setRoot( nc.getID() );
        }

        // write root chunk to DXMem
        chunkService.put().put( rc );

        //
        // register metadata chunk in nameservice, name will be myNodeID
        //
        NameserviceService nameService = taskContext.getDXRAMServiceAccessor().getService( NameserviceService.class );

        // we cannot simply convert the NodeID to a string as it might be negative and then the name would be too long
        String nodeIDstr = Integer.toHexString(0xFFFF & myNodeID);
        nameService.register(rc, nodeIDstr);

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
