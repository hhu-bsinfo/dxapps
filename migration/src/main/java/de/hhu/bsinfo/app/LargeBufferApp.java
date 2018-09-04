package de.hhu.bsinfo.app;

import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import org.apache.commons.cli.CommandLine;

import java.util.Random;

public class LargeBufferApp extends AbstractApplication {

    private static final int NUM_CHUNKS = 2;

    public static final int CHUNK_SIZE = 1024 * 1024 * 4;

    private Random m_random = new Random();

    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "LargeBufferApp";
    }

    @Override
    public boolean useConfigurationFile() {
        return false;
    }

    @Override
    public void main(final CommandLine p_commandLine) {

//        ChunkService chunkService = getService(ChunkService.class);
//
//        BootService bootService = getService(BootService.class);
//
//        MigrationService migrationService = getService(MigrationService.class);
//
//        NetworkService networkService = getService(NetworkService.class);
//
//        Optional<Short> targetOptional = bootService.getSupportingNodes(NodeCapabilities.STORAGE).stream()
//                .filter(id -> id != bootService.getNodeID())
//                .findFirst();
//
//        while (!targetOptional.isPresent()) {
//
//            targetOptional = bootService.getSupportingNodes(NodeCapabilities.STORAGE).stream()
//                    .filter(id -> id != bootService.getNodeID())
//                    .findFirst();
//        }
//
//        short target = targetOptional.get();
//
//        for (int i = 0; i < NUM_CHUNKS; i++) {
//
//            byte[] bytes = new byte[CHUNK_SIZE];
//
//            m_random.nextBytes(bytes);
//
//            DSByteArray byteArray = new DSByteArray(bytes);
//
//            chunkService.create(byteArray);
//
//            chunkService.put(byteArray);
//        }
//
//        migrationService.migrateRange(1, NUM_CHUNKS, target);
    }

    @Override
    public void signalShutdown() {

    }
}
