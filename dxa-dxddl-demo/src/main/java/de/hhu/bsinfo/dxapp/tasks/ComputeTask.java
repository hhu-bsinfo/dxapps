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
import de.hhu.bsinfo.dxmem.data.ChunkID;

public class ComputeTask implements Task {

    public ComputeTask() { }

    @Override
    public int execute(TaskContext taskContext) {
        NameserviceService nameService = taskContext.getDXRAMServiceAccessor().getService( NameserviceService.class );
        ChunkService chunkService = taskContext.getDXRAMServiceAccessor().getService(ChunkService.class);
        short myNodeID = taskContext.getCtxData().getOwnNodeId();

        System.out.printf("  DxddlDemoApplication (slave): ComputeTask.execute called.\n");

        //
        // get our root chunk from name service
        //
        String nodeIDstr = Integer.toHexString(0xFFFF & myNodeID);
        long metaChunkID = nameService.getChunkID(nodeIDstr, 1000);

        RootChunk rc = new RootChunk( metaChunkID );
        chunkService.get().get( rc );

        //
        // calculate sum of all list entries
        //
        int sum = 0;

        NodeChunk nc = new NodeChunk( rc.getRoot() );
        while (nc.getID() != ChunkID.INVALID_ID) {
            chunkService.get().get( nc );

            sum += nc.getVal();

            nc.setID( nc.getNext() );
        }

        System.out.printf("  DxddlDemoApplication (slave): ComputeTask sum = %d\n", sum);
        System.out.printf("  DxddlDemoApplication (slave): ComputeTask done.\n");
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
