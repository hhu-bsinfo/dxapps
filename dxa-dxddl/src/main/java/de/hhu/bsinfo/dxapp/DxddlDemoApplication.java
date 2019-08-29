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
import de.hhu.bsinfo.dxapp.tasks.ComputeTask;
import de.hhu.bsinfo.dxapp.chunks.HeadChunk;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxmem.data.ChunkID;

/**
 * "DXDDL Demo" example DXRAM application.
 *
 * @author Michael Schoettner, michael.schoettner@hhu.de, 19.08.2019
 */
public class DxddlDemoApplication extends Application {
    public final static int NAME_SERVICE_LOOKUP_TIMEOUT = 2000;    // 2s

    BootService bootService;
    MasterSlaveComputeService computeService;
    ChunkService chunkService;
    HeadChunk[] headChunks;     // head chunks on all slaves (for accessing data)


    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "DxddlDemoApplication";
    }

    // execute initialization task on each slave
    private void slavesInit() {
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
        System.out.printf("  DxddlDemoApplication (master): slavesInit done.\n");
    }

    // execute compute slavesCompute on each slave
    private void slavesCompute() {
        ComputeTask generateDataTask = new ComputeTask();
        TaskScript generateDataTaskScript = new TaskScript(generateDataTask);
        TaskScriptState inputState = computeService.submitTaskScript(generateDataTaskScript, (short) 0);

        // wait for all slave tasks to finish
        while (!inputState.hasTaskCompleted()) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException ignore) {
            }
        }
        System.out.printf("  DxddlDemoApplication (master): slavesCompute done.\n");
    }

    // get head chunks from all slaves
    private void getResultsFromSlaves() {
        NameserviceService nameService = getService( NameserviceService.class );
        ArrayList<Short> connectedSlaves = computeService.getStatusMaster((short) 0).getConnectedSlaves();
        ChunkService chunkService = getService(ChunkService.class);

        headChunks = new HeadChunk[ connectedSlaves.size() ];
        for (int i = 0; i < connectedSlaves.size(); i++) {
            //
            // get head chunk ID of each slave from naming service (the name is the NodeID of each slave)
            //
            // we cannot simply convert the NodeID to a string as it might be negative and then the name would be too long
            String nodeIDstr = Integer.toHexString(0xFFFF & connectedSlaves.get(i));
            long result = nameService.getChunkID(nodeIDstr, NAME_SERVICE_LOOKUP_TIMEOUT);
            if (result == ChunkID.INVALID_ID) {
                System.out.printf("(master) Nameservice lookup failed for slave %s.", connectedSlaves.get(i) );
            }
            else {
               headChunks[i] = new HeadChunk( result );
               chunkService.get().get( headChunks[i] );
               System.out.printf("  DxddlDemoApplication (master): result of slave %d = %d\n", i, headChunks[i].getSum() );
            }
        }
    }

    @Override
    public void main(final String[] p_args) {
        bootService = getService( BootService.class );
        computeService = getService( MasterSlaveComputeService.class );
        chunkService = getService(ChunkService.class);

        System.out.printf("\n");

        // master submits & coordinates slave tasks
        if ( computeService.getComputeRole() == ComputeRole.MASTER ) {
            System.out.printf("  DxddlDemoApplication (master): main\n");
            slavesInit();
            slavesCompute();
            getResultsFromSlaves();
        }
        else {
            System.out.printf("  DxddlDemoApplication (slave): main\n");
        }
    }

    @Override
    public void signalShutdown() {
        // Interrupt any flow of your application and make sure it shuts down.
        // Do not block here or wait for something to shut down. Shutting down of your application
        // must be execute asynchronously
    }
}
