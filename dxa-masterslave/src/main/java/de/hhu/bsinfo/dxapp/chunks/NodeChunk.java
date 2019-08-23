package de.hhu.bsinfo.dxapp.chunks;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import java.util.Random;

/**
 * Chunk to store an entry of a chained list
 */
public class NodeChunk extends AbstractChunk {

    private int  m_val;     // value
    private long m_next;    // ChunkID of next node

    // needed for creating new chunk
    public NodeChunk(int val, long next) {
        super();
        m_val  = val;
        m_next = next;
    }

    // needed for reading object from DXMem
    public NodeChunk(long p_chunkID) {
        super(p_chunkID);
    }

    public int getVal() {
        return m_val;
    }

    public long getNext() {
        return m_next;
    }

    public void setVal(int val) {
      m_val = val;
    }

    public void setNext(long next) {
       m_next = next;
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeInt(m_val);
        p_exporter.writeLong(m_next);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_val = p_importer.readInt(m_val);
        m_next  = p_importer.readLong(m_next);
    }

    @Override
    public int sizeofObject() {
        return Integer.BYTES + Long.BYTES;
    }
}
