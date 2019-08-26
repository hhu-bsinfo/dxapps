package de.hhu.bsinfo.dxapp.chunks;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;

/**
 * Chunk to store head info accessed by master
 */
public class HeadChunk extends AbstractChunk {
    private long m_head_NodeChunk;      // ChunkID of root entry of list (type NodeChunk)
    private int  m_sum;                 // sum of all entries in list referenced by m_root_NodeChunk

    // needed for creating new chunk
    public HeadChunk() { }

    // needed for creating new chunk
    public HeadChunk(long root, int sum) {
        super();

        m_head_NodeChunk = root;
        m_sum = sum;
    }

    // needed for reading/writing existing object from/to DXMem
    public HeadChunk(long p_chunkID) {
        super(p_chunkID);
    }

    public long getHead() {
        return m_head_NodeChunk;
    }

    public int getSum() {
        return m_sum;
    }

    public void setHead (long head) {
        m_head_NodeChunk = head;
    }

    public void setSum (int sum) {
        m_sum = sum;
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeLong(m_head_NodeChunk);
        p_exporter.writeInt(m_sum);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_head_NodeChunk = p_importer.readLong(m_head_NodeChunk);
        m_sum = p_importer.readInt(m_sum);
    }

    @Override
    public int sizeofObject() {
        return Long.BYTES+Integer.BYTES;
    }
}
