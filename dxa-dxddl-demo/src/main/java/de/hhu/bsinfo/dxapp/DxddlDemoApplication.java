package de.hhu.bsinfo.dxapp;

import java.util.Arrays;
import java.util.ArrayList;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;
import de.hhu.bsinfo.dxram.ms.TaskScriptState;
import de.hhu.bsinfo.dxram.ms.ComputeRole;
import de.hhu.bsinfo.dxram.ms.script.TaskScript;
import de.hhu.bsinfo.dxapp.tasks.InitTask;
import de.hhu.bsinfo.dxapp.chunks.RootChunk;
import de.hhu.bsinfo.dxram.chunk.ChunkService;

/**
 * "DXDDL Demo" example DXRAM application.
 *
 * @author Michael Schoettner, michael.schoettner@hhu.de, 19.08.2019
 */
public class DxddlDemoApplication extends Application {
    RootChunk[] rootChunks;     // root chunks on all slaves (for accessing data)


    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "DxddlDemoApplication";
    }

    // execute initialization task on each slave
    private void initAllSlaves() {
        InitTask generateDataTask = new InitTask();
        TaskScript generateDataTaskScript = new TaskScript(generateDataTask);
        TaskScriptState inputState = computeService.submitTaskScript(generateDataTaskScript, (short) 0);

        // wait for all slave tasks to finish
        while (!inputState.hasTaskCompleted()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ignore) {
            }
        }
        System.out.printf("  DxddlDemoApplication (master): initAllSlaves done.\n");
    }

    // get root chunks from all slaves
    private void getRootChunksFromAllSlaves() {
        NameserviceService nameService = getService( NameserviceService.class );
        MasterSlaveComputeService computeService = getService( MasterSlaveComputeService.class );
        ArrayList<Short> connectedSlaves = computeService.getStatusMaster((short) 0).getConnectedSlaves();
        ChunkService chunkService = getService(ChunkService.class);

        rootChunks = new RootChunk[ connectedSlaves.size() ];
        for (int i = 0; i < connectedSlaves.size(); i++) {
            System.out.printf("  DxddlDemoApplication (master): name service lookup %d.\n", connectedSlaves.get(i));

            //
            // get root chunk ID of each slave from naming service (the name is the NodeID of each slave)
            //
            // we cannot simply convert the NodeID to a string as it might be negative and then the name would be too long
            String nodeIDstr = Integer.toHexString(0xFFFF & connectedSlaves.get(i));
            long result = nameService.getChunkID(nodeIDstr, 1000);

            rootChunks[i] = new RootChunk( result );
            chunkService.get().get( rootChunks[i] );
            System.out.printf("  DxddlDemoApplication (master): metadata from slave %d = %d\n", i, rootChunks[i].getDummy() );
        }
    }

        // code executed on master
    private void master() {

        initAllSlaves();

        getRootChunksFromAllSlaves();



    }


    @Override
    public void main(final String[] p_args) {
        BootService bootService = getService( BootService.class );
        MasterSlaveComputeService computeService = getService( MasterSlaveComputeService.class );

        System.out.printf("\n");
        System.out.printf("  DxddlDemoApplication: main\n");

        if ( computeService.getComputeRole() == ComputeRole.MASTER ) {
            master();
        }
    }

    @Override
    public void signalShutdown() {
        // Interrupt any flow of your application and make sure it shuts down.
        // Do not block here or wait for something to shut down. Shutting down of your application
        // must be execute asynchronously
    }
}
