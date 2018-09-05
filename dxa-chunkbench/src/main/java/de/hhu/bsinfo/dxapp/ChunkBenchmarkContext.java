package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.benchmark.BenchmarkContext;
import de.hhu.bsinfo.dxmem.core.CIDTableStatus;
import de.hhu.bsinfo.dxmem.core.HeapStatus;
import de.hhu.bsinfo.dxmem.core.LIDStoreStatus;
import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxram.chunk.ChunkDebugService;
import de.hhu.bsinfo.dxram.chunk.ChunkLocalService;
import de.hhu.bsinfo.dxram.chunk.ChunkService;

public class ChunkBenchmarkContext implements BenchmarkContext {
    private final ChunkService m_default;
    private final ChunkLocalService m_local;
    private final ChunkDebugService m_debug;

    public ChunkBenchmarkContext(final ChunkService p_default, final ChunkLocalService p_local,
            final ChunkDebugService p_debug) {
        m_default = p_default;
        m_local = p_local;
        m_debug = p_debug;
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
