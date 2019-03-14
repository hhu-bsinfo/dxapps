package de.hhu.bsinfo.dxapp;

import picocli.CommandLine;

import de.hhu.bsinfo.dxram.app.Application;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkDebugService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.engine.DXRAMVersion;
import de.hhu.bsinfo.dxram.generated.BuildConfig;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.stats.StatisticsService;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

public class ChunkBenchmark extends Application {
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
        CommandLine.run(new BenchmarkCommand(new ChunkBenchmarkContext(p_args, getService(ApplicationService.class),
                getService(BootService.class), getService(ChunkService.class), getService(ChunkLocalService.class),
                getService(ChunkDebugService.class), getService(NameserviceService.class),
                getService(StatisticsService.class), getService(SynchronizationService.class))), p_args);
    }

    @Override
    public void signalShutdown() {

    }
}
