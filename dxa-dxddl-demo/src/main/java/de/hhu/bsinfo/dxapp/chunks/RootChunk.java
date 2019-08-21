package de.hhu.bsinfo.dxapp.chunks;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;


/**
 * Chunk to store root info to be accessed by master
 */

public class RootChunk extends AbstractChunk {

    private int m_mySlaveIndex;

    public RootChunk(int mySlaveIndex) {
        super();
        m_mySlaveIndex = mySlaveIndex;
    }

    public double getMySlaveIndex() {
        return m_mySlaveIndex;
    }

    public void setMySlaveIndex (int mySlaveIndex) {
        m_mySlaveIndex = mySlaveIndex;
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeInt(m_mySlaveIndex);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_mySlaveIndex = p_importer.readInt(m_mySlaveIndex);
    }

    @Override
    public int sizeofObject() {
        return Integer.BYTES;
    }
}
