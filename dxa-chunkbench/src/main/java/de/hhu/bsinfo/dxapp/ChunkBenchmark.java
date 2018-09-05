package de.hhu.bsinfo.dxapp;

import picocli.CommandLine;

import de.hhu.bsinfo.dxram.app.AbstractApplication;
import de.hhu.bsinfo.dxram.chunk.ChunkDebugService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;

public class ChunkBenchmark extends AbstractApplication {
    @Override
    public DXRAMVersion getBuiltAgainstVersion() {
        return BuildConfig.DXRAM_VERSION;
    }

    @Override
    public String getApplicationName() {
        return "ChunkBenchmark";
    }

    @Override
    public void main(final String[] p_args) {
        // TODO bootstrap wait for peers etc -> common helper class?

        // TODO update ChunkIDRanges after load phase for benchmark -> get all chunk ranges from ChunkService

        // TODO batch X objects on create/get/put calls -> add this to benchmark

        CommandLine.run(new BenchmarkCommand(new ChunkBenchmarkContext(getService(ChunkService.class),
                getService(ChunkLocalService.class), getService(ChunkDebugService.class))), p_args);
    }

    @Override
    public void signalShutdown() {
        // TODO
    }
}
