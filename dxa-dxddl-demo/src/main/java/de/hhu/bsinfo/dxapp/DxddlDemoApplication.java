package de.hhu.bsinfo.dxapp;

import java.util.Arrays;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxutils.NodeID;

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
        BootService bootService = getService(BootService.class);

        System.out.printf("\n");
        System.out.printf("  DxddlDemoApplication\n", );
        System.out.printf("\n");

        // Put your application code running on the DXRAM node/peer here
    }

    @Override
    public void signalShutdown() {
        // Interrupt any flow of your application and make sure it shuts down.
        // Do not block here or wait for something to shut down. Shutting down of your application
        // must be execute asynchronously
    }
}
