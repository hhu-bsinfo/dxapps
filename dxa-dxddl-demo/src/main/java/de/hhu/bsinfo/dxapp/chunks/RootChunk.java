package de.hhu.bsinfo.dxapp.chunks;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;

/**
 * Chunk to store root info accessed by master
 */
public class RootChunk extends AbstractChunk {
    private long m_root_NodeChunk;      // ChunkID of root entry of list (type NodeChunk)
    private int  m_sum;                 // sum of all entries in list referenced by m_root_NodeChunk

    // needed for creating new chunk
    public RootChunk() { }

    // needed for creating new chunk
    public RootChunk(long root, int sum) {
        super();

        m_root_NodeChunk = root;
        m_sum = sum;
    }

    // needed for reading/writing existing object from/to DXMem
    public RootChunk(long p_chunkID) {
        super(p_chunkID);
    }

    public long getRoot() {
        return m_root_NodeChunk;
    }

    public int getSum() {
        return m_sum;
    }

    public void setRoot (long root) {
        m_root_NodeChunk = root;
    }

    public void setSum (int sum) {
        m_sum = sum;
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeLong(m_root_NodeChunk);
        p_exporter.writeInt(m_sum);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_root_NodeChunk = p_importer.readLong(m_root_NodeChunk);
        m_sum = p_importer.readInt(m_sum);
    }

    @Override
    public int sizeofObject() {
        return Long.BYTES+Integer.BYTES;
    }
}
