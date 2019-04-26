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

    private boolean m_useMultiOps = true;

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

    public void setUseMultiOps(boolean useMultiOps) {
        this.m_useMultiOps = useMultiOps;
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
    public void create(long[] p_cids, int[] p_sizes) {
        m_local.createLocal().createSizes(p_cids, p_sizes);
    }

    @Override
    public void dump(String p_outFile) {

    }

    @Override
    public void get(AbstractChunk[] p_chunks) {
        if(m_useMultiOps) {
            m_default.get().get(p_chunks);
        } else {
            for(AbstractChunk chunk : p_chunks) {
                m_default.get().get(chunk);
            }
        }
    }

    @Override
    public void put(AbstractChunk[] p_chunks) {
        if(m_useMultiOps) {
            m_default.put().put(p_chunks);
        } else {
            for(AbstractChunk chunk : p_chunks) {
                m_default.put().put(chunk);
            }
        }
    }

    @Override
    public void remove(AbstractChunk[] p_chunks) {
        if(m_useMultiOps) {
            m_default.remove().remove(p_chunks);
        } else {
            for(AbstractChunk chunk : p_chunks) {
                m_default.remove().remove(chunk);
            }
        }
    }
}
