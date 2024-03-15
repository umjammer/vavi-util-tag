/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2;

import java.io.IOException;


/**
 * ID3v2HeaderV230.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051205 nsano initial version <br>
 */
public abstract class ID3v2Header {

    /** */
    protected ID3v2Header() {
    }

    /** */
    public int getVersion() {
        return version;
    }

    /** */
    public int getRevision() {
        return revision;
    }

    /**
     * Reads header from stream <code>in</code>
     * Header must start at file position.
     *
     * @param in Stream to read from
     * @throws ID3v2IllegalVersionException If tag has a revision higher than
     *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
     * @throws ID3v2MissingHeaderException If file does not contain an ID3v2 header
     * @throws IOException If an I/O error occurs
     */
    protected void inject(byte[] head) throws ID3v2Exception, IOException {

        // check if header
        if (!isHeader(head)) {
            throw new ID3v2Exception("missing header");
        }

        // so we have a valid header
        // check version
        version = head[3];
        revision = head[4];

        // read & parse flags
        this.flag = head[5];

        // Last, read size. Size is stored in 4 bits, which all have their highest
        // bit set to 0 (unsynchronization)
        size = (head[9] & 0xff) + ((head[8] & 0xff) << 7) + ((head[7] & 0xff) << 14) + ((head[6] & 0xff) << 21);
    }

    /**
     * Is unsynchronization bit set?
     */
    public boolean getUnsynchronization() {
        return unsynch;
    }

    /**
     * Set / unset unsynchronization bit
     *
     * @param act True: Set unsynchronization bit
     */
    public void setUnsynchronization(boolean act) {
        unsynch = act;
    }

    /**
     * Get length of tag
     *
     * @return Length of tag without header (complete length - 10)
     */
    public int getTagSize() {
        return size;
    }

    /**
     * Set length if tag
     */
    public void setTagSize(int size) {
        this.size = size;
    }

    /** */
    public int getFlag() {
        return flag;
    }

    /** */
    private int version;
    /** */
    private int revision;

    /** */
    protected boolean unsynch;
    /** */
    protected int flag;
    /** */
    protected int size;

    /**
     * Convert header to array of bytes
     *
     * @return Header as bytes, ready to write
     */
    public abstract byte[] getBytes();

    /**
     * Checks if bytes contain a correct header
     *
     * @param head Array of bytes to be checked
     * @return true if header is correct
     */
    protected static boolean isHeader(byte[] head) {
        // head must be 10 bytes long
        if (head.length != 10) {
            return false;
        }

        // must start with ID3
        if ((head[0] != 'I') || (head[1] != 'D') || (head[2] != '3')) {
            return false;
        }

        // next two bytes must be smaller than 255
        if ((head[3] == (byte) 255) || (head[4] == (byte) 255)) {
            return false;
        }

        // for safety's sake (who knows what future versions will bring),
        // the flags are not checked
        // last 4 bytes must be smaller than 128 (first bit set to 0)
        if (((head[6] & 0xff) >= 128) || ((head[7] & 0xff) >= 128) || ((head[8] & 0xff) >= 128) || ((head[9] & 0xff) >= 128)) {
            return false;
        }

        return true;
    }
}
