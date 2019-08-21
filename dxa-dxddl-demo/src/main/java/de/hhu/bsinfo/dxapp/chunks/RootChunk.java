package de.hhu.bsinfo.dxapp.chunks;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import java.util.Random;

/**
 * Chunk to store root info to be accessed by master
 */

public class RootChunk extends AbstractChunk {

    private int m_mySlaveIndex;
    private int m_dummy;

    public RootChunk(int mySlaveIndex) {
        super();

        Random rand = new Random();
        m_mySlaveIndex = mySlaveIndex;
        m_dummy = rand.nextInt(32768);
    }

    public RootChunk(long p_chunkID) {
        super(p_chunkID);
    }

    public int getMySlaveIndex() {
        return m_mySlaveIndex;
    }
    public int getDummy() {
        return m_dummy;
    }

    public void setMySlaveIndex (int mySlaveIndex) {
        m_mySlaveIndex = mySlaveIndex;
    }
    public void setDummy (int val) {
        m_dummy = val;
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeInt(m_mySlaveIndex);
        p_exporter.writeInt(m_dummy);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_mySlaveIndex = p_importer.readInt(m_mySlaveIndex);
        m_dummy = p_importer.readInt(m_dummy);
    }

    @Override
    public int sizeofObject() {
        return 2*Integer.BYTES;
    }
}
