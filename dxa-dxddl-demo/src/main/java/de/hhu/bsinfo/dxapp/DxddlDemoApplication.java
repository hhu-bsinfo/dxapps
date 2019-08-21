package de.hhu.bsinfo.dxapp;

import java.util.Arrays;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxutils.NodeID;
import de.hhu.bsinfo.dxram.ms.MasterSlaveComputeService;
import de.hhu.bsinfo.dxram.ms.script.TaskScript;
import de.hhu.bsinfo.dxapp.tasks.*;

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

    @Override
    public void main(final String[] p_args) {
        BootService bootService = getService( BootService.class );
        NameserviceService nameService = getService( NameserviceService.class );
        MasterSlaveComputeService computeService = getService( MasterSlaveComputeService.class );
        String chunkName = "1";

        System.out.printf("\n");
        System.out.printf("  DxddlDemoApplication\n\n");

        if ( computeService.getComputeRole() == ComputeRole.MASTER ) {
            System.out.printf("  DxddlDemoApplication: master preparing data generation tasks.\n");

            GenerateDataTask generateDataTask = new GenerateDataTask();
            TaskScript generateDataTaskScript = new TaskScript( generateDataTask );
            TaskScriptState inputState = computeService.submitTaskScript( generateDataTaskScript, (short) 0);

            // wait for all tasks to finish
            while (!inputState.hasTaskCompleted()) {
                try { Thread.sleep(100); }
                catch (final InterruptedException ignore) { }
            }
            System.out.printf("  DxddlDemoApplication: master data generation tasks finished.\n");
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
