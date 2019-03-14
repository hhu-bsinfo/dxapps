package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxutils.NodeID;

/**
 * A simple example application, which stores a string into a chunk and retrieves it afterwards.
 *
 * Usage: dxa-chunkexample.jar 'target-id' 'string'
 *  -target-id: specifies on which node the string shall be stored (e.g. b1bd)
 *  -string: the string to be stored (optional)
 *
 * @author Fabian Ruhland, fabian.ruhland@hhu.de, 12.03.2019
 */
public class ChunkApplication extends Application {

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "ChunkExample";
    }

    @Override
    public void main(final String[] p_args) {
        BootService bootService = getService(BootService.class);

        System.out.printf("\n  Running '%s' on node %s!\n\n", getApplicationName(), NodeID.toHexStringShort(bootService.getNodeID()));

        if(p_args.length == 0) {
            System.out.printf("  No target node given!\n\n");
            return;
        }

        String message = "Hello from a Chunk!";

        if(p_args.length > 1) {
            message = p_args[1];
        }

        if(p_args.length > 1) {
            message = p_args[1];
        }

        short targetId = (short) Integer.parseUnsignedInt(p_args[0], 16);

        long chunkId = storeStringInChunk(message, targetId);

        if(chunkId == ChunkID.INVALID_ID) {
            return;
        }

        String retrievedMessage = getStringFromChunk(chunkId);

        System.out.printf("  Retrieved message '%s'\n\n", retrievedMessage);

        System.out.printf("  '%s' finished!\n\n", getApplicationName());
    }

    @Override
    public void signalShutdown() {
        // Interrupt any flow of your application and make sure it shuts down.
        // Do not block here or wait for something to shut down. Shutting down of your application
        // must be execute asynchronously
    }

    private long storeStringInChunk(final String p_string, final short p_targetId) {
        ChunkService chunkService = getService(ChunkService.class);

        ChunkString chunk = new ChunkString(p_string);

        if(chunkService.create().create(p_targetId, chunk) == 1) {
            System.out.printf("  Created chunk '%s'!\n", chunk);
        } else {
            System.out.printf("  Error creating chunk!\n\n");
            return ChunkID.INVALID_ID;
        }

        if(chunkService.put().put(chunk)) {
            System.out.printf("  Stored data in chunk '%s'!\n", chunk);
            System.out.printf("  Content: '%s'\n\n", chunk.getString());
        } else {
            System.out.printf("  Error while storing data in chunk!\n\n");
            return ChunkID.INVALID_ID;
        }

        return chunk.getID();
    }

    private String getStringFromChunk(final long chunkId) {
        ChunkService chunkService = getService(ChunkService.class);

        ChunkString chunk = new ChunkString(chunkId);

        if(chunkService.get().get(chunk)) {
            System.out.printf("  Retrieved chunk '%s'!\n", chunk);

            return chunk.getString();
        } else {
            System.out.printf("  Error retrieving chunk '%s'!\n\n", chunk);

            return "";
        }
    }
}
