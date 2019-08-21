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

/**
 * "DXDDL Demo" example DXRAM application.
 *
 * @author Michael Schoettner, michael.schoettner@hhu.de, 19.08.2019
 */
public class DxddlDemoApplication extends Application {
    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "DxddlDemoApplication";
    }

    // code executed on master
    private void master() {
        NameserviceService nameService = getService( NameserviceService.class );
        MasterSlaveComputeService computeService = getService( MasterSlaveComputeService.class );
        ArrayList<Short> connectedSlaves = computeService.getStatusMaster((short) 0).getConnectedSlaves();

        System.out.printf("  DxddlDemoApplication: master firing init task on each slave.\n");

        //
        // generate data on each slave
        //
        InitTask generateDataTask = new InitTask();
        TaskScript generateDataTaskScript = new TaskScript( generateDataTask );
        TaskScriptState inputState = computeService.submitTaskScript( generateDataTaskScript, (short) 0);

        // wait for all tasks to finish
        while (!inputState.hasTaskCompleted()) {
            try { Thread.sleep(100); }
            catch (final InterruptedException ignore) { }
        }
        System.out.printf("  DxddlDemoApplication: all initialization tasks finished.\n");


       // MetaChunk[] metaChunks = new MetaChunk[ connectedSlaves.size() ];
        for (int i = 0; i < connectedSlaves.size(); i++) {
            System.out.printf("  DxddlDemoApplication: name service lookup %d.\n", connectedSlaves.get(i));

            // get root chunk of each slave
            long result = nameService.getChunkID(new Short(connectedSlaves.get(i)).toString(), 1000);
            System.out.printf("  DxddlDemoApplication: name service lookup result = %ld.\n", result);


//            metaChunks[i] = new MetaChunk( ChunkID.getChunkID(connectedSlaves.get(i),0) );
  //          chunkService.get().get( metaChunks[i] );
    //        System.out.printf("  DxddlDemoApplication: metadata from slave %d = %d\n", i, metaChunks[i].mySlaveIndex);
        }


    }


    @Override
    public void main(final String[] p_args) {
        BootService bootService = getService( BootService.class );
        MasterSlaveComputeService computeService = getService( MasterSlaveComputeService.class );

        System.out.printf("\n");
        System.out.printf("  DxddlDemoApplication\n\n");

        if ( computeService.getComputeRole() == ComputeRole.MASTER ) {
            master();
        }


   /*     System.out.println("Register chunk " + chunkName + " in NameService");
        nameService.register(100, chunkName);

        System.out.println("Lookup entry " + chunkName +" in NameService");
        long result = nameService.getChunkID(chunkName, 1000);
        System.out.println("   returned chunkID = " + result);

        System.out.println("ComputeRole");
        if ( computeService.getComputeRole() == ComputeRole.MASTER )
           System.out.println("   Master");
       else
           System.out.println("    Slave");
*/
        // Put your application code running on the DXRAM node/peer here
    }

    @Override
    public void signalShutdown() {
        // Interrupt any flow of your application and make sure it shuts down.
        // Do not block here or wait for something to shut down. Shutting down of your application
        // must be execute asynchronously
    }
}
