package de.hhu.bsinfo.dxapp;

import de.hhu.bsinfo.dxmem.data.AbstractChunk;
import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxutils.serialization.Exporter;
import de.hhu.bsinfo.dxutils.serialization.Importer;
import de.hhu.bsinfo.dxutils.serialization.ObjectSizeUtil;

import java.security.InvalidParameterException;

/**
 * Implementation of a chunk, which stores a single string.
 *
 * @author Fabian Ruhland, fabian.ruhland@hhu.de, 12.03.2019
 */
public class ChunkString extends AbstractChunk {

    private String m_string;

    /**
     * Constructor
     * Sets the chunk id to invalid.
     *
     * @param p_string
     *         The string to be stored in the chunk
     */
    public ChunkString(String p_string) {
        super();

        if (p_string == null) {
            throw new InvalidParameterException("p_buffer == null");
        }

        this.m_string = p_string;
    }

    /**
     * Constructor
     * Creates a chunk with an uninitialized string, which can be used for get()-operations.
     *
     * @param p_cid
     *         ID the chunk is assigned to
     */
    public ChunkString(final long p_cid) {
        super(p_cid);
    }

    /**
     * Constructor
     * Creates a chunk with an initialized string.
     *
     * @param p_cid
     *         ID the chunk is assigned to
     * @param p_string
     *         The string to be stored in the chunk
     */
    public ChunkString(final long p_cid, String p_string) {
        super(p_cid);

        if (p_string == null) {
            throw new InvalidParameterException("p_string == null");
        }

        this.m_string = p_string;
    }

    /**
     * Get the string, which is stored in the chunk.
     *
     * @return The string
     */
    public String getString() {
        return m_string;
    }

    @Override
    public String toString() {
        return "ChunkString[" + ChunkID.toHexString(getID()) + ", " + getState() + ", " + m_string.length() + ']';
    }

    @Override
    public boolean equals(final Object p_other) {
        if (p_other instanceof ChunkString) {
            ChunkString other = (ChunkString) p_other;

            return m_string.equals(other.m_string);
        } else {
            return false;
        }
    }

    @Override
    public void exportObject(Exporter p_exporter) {
        p_exporter.writeString(m_string);
    }

    @Override
    public void importObject(Importer p_importer) {
        m_string = p_importer.readString(m_string);
    }

    @Override
    public int sizeofObject() {
        return ObjectSizeUtil.sizeofString(m_string);
    }
}
