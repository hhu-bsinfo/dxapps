package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.benchmark.BenchmarkContext;
import de.hhu.bsinfo.dxmem.core.CIDTableStatus;
import de.hhu.bsinfo.dxmem.core.HeapStatus;
import de.hhu.bsinfo.dxmem.core.LIDStoreStatus;
import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxram.boot.BootService;
import de.hhu.bsinfo.dxram.chunk.ChunkDebugService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.nameservice.NameserviceService;
import de.hhu.bsinfo.dxram.stats.StatisticsService;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;

public class ChunkBenchmarkContext implements BenchmarkContext {
    private final String[] m_appArgs;
    private final ApplicationService m_app;
    private final BootService m_boot;
    private final ChunkService m_default;
    private final ChunkLocalService m_local;
    private final ChunkDebugService m_debug;
    private final NameserviceService m_name;
    private final StatisticsService m_statistics;
    private final SynchronizationService m_sync;

    public ChunkBenchmarkContext(final String[] p_appArgs, final ApplicationService p_application,
            final BootService p_boot, final ChunkService p_default, final ChunkLocalService p_local,
            final ChunkDebugService p_debug, final NameserviceService p_name, final StatisticsService p_statistics,
            final SynchronizationService p_sync) {
        m_appArgs = p_appArgs;
        m_app = p_application;
        m_boot = p_boot;
        m_default = p_default;
        m_local = p_local;
        m_debug = p_debug;
        m_name = p_name;
        m_statistics = p_statistics;
        m_sync = p_sync;
    }

    public String[] getAppArgs() {
        return m_appArgs;
    }

    public ApplicationService getApplicationService() {
        return m_app;
    }

    public BootService getBootService() {
        return m_boot;
    }

    public ChunkService getChunkService() {
        return m_default;
    }

    public NameserviceService getNameserviceService() {
        return m_name;
    }

    public StatisticsService getStatisticsService() {
        return m_statistics;
    }

    public SynchronizationService getSyncService() {
        return m_sync;
    }

    @Override
    public HeapStatus getHeapStatus() {
        return m_default.status().getStatus().getHeapStatus();
    }

    @Override
    public CIDTableStatus getCIDTableStatus() {
        return m_default.status().getStatus().getCIDTableStatus();
    }

    @Override
    public LIDStoreStatus getLIDStoreStatus() {
        return m_default.status().getStatus().getLIDStoreStatus();
    }

    @Override
    public long create(final int p_size) {
        long[] result = new long[1];
        m_local.createLocal().create(result, 1, p_size);
        return result[0];
    }

    @Override
    public void dump(final String p_outFile) {
        m_debug.dump().dumpChunkMemory(p_outFile);
    }

    @Override
    public void get(final AbstractChunk p_chunk) {
        m_default.get().get(p_chunk);
    }

    @Override
    public void put(final AbstractChunk p_chunk) {
        m_default.put().put(p_chunk);
    }

    @Override
    public void remove(final AbstractChunk p_chunk) {
        m_default.remove().remove(p_chunk);
    }
}
