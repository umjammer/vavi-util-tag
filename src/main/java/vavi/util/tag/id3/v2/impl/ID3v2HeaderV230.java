/*
 * ID3v2Header.java
 *
 * $Id: ID3v2Header.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
 *
 * de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
 * Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 */

package vavi.util.tag.id3.v2.impl;

import java.io.IOException;
import java.io.Serializable;

import vavi.util.tag.id3.v2.ID3v2Exception;
import vavi.util.tag.id3.v2.ID3v2Header;


/**
 * This class contains an ID3v2 header.
 */
public class ID3v2HeaderV230 extends ID3v2Header implements Serializable {

    /** jdk1.4 logger */
//    private static Logger logger = Logger.getLogger(ID3v2HeaderV230.class.getName());

    /**
     * ID3v2 version
     */
    private final static byte VERSION = 3;

    /**
     * ID3v2 revision
     */
    private final static byte REVISION = 0;

    /**
     * Create a new (empty) header
     */
    public ID3v2HeaderV230() {
        this(false, false, false, 0);
    }

    /**
     * Build a ID3v2 header
     *
     * @param version ID3v2 version
     * @param revision ID3v2 revision
     * @param unsynch Use unsynchronization scheme?
     * @param extended_header Use extended header?
     * @param experimental Is experimental?
     * @param length ID3v2 tag length
     */
    public ID3v2HeaderV230(boolean unsynch, boolean extended_header, boolean experimental, int length) {
        this.unsynch = unsynch;
        this.extended_header = extended_header;
        this.experimental = experimental;
        this.size = length;
    }

    /**
     * Creates an ID3v2 header from an input stream.
     *
     * @param in Stream to read from
     * @throws ID3v2IllegalVersionException If tag has a revision higher than
     *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
     * @throws ID3v2MissingHeaderException If file does not contain an ID3v2 header
     * @throws IOException If an I/O error occurs
     */
    public ID3v2HeaderV230(byte[] head) throws ID3v2Exception, IOException {
        inject(head);

        unsynch = (flag & FLAG_UNSYNCHRONIZATION) > 0;
        extended_header = (flag & FLAG_EXTENDED_HEADER) > 0;
        experimental = (flag & FLAG_EXPERIMENTAL) > 0;
//logger.info("unsynch: " + unsynch);
//logger.info("extended_header: " + extended_header);
//logger.info("size: " + size);
    }

    /**
     * Is extended header present?
     */
    public boolean hasExtendedHeader() {
        return extended_header;
    }

    /**
     * Set / unset extended header present
     *
     * @param act True: Set extended header present bit
     */
    public void setExtendedHeader(boolean act) {
        extended_header = act;
    }

    /**
     * Is experimental?
     */
    public boolean getExperimental() {
        return experimental;
    }

    /**
     * Set / unset experimental
     *
     * e   * @param act True: Set experimental bit
     */
    public void setExperimental(boolean act) {
        experimental = act;
    }

    /**
     * Convert header to array of bytes
     *
     * @return Header as bytes, ready to write
     */
    public byte[] getBytes() {
        byte[] work = new byte[10];

        work[0] = 'I';
        work[1] = 'D';
        work[2] = '3';

        work[3] = VERSION;
        work[4] = REVISION;

        byte flag = 0;
        if (unsynch == true) {
            flag += FLAG_UNSYNCHRONIZATION;
        }
        if (extended_header == true) {
            flag += FLAG_EXTENDED_HEADER;
        }
        if (experimental == true) {
            flag += FLAG_EXPERIMENTAL;
        }
        work[5] = flag;

        // create length bytes manually ("unsynchronized")
        for (int i = 0; i < 4; i++) {
            work[i + 6] = (byte) ((size >> ((3 - i) * 7)) & 127);
        }

        return work;
    }

    /** */
    protected boolean extended_header;
    /** */
    protected boolean experimental;
    /** */
    protected static final byte FLAG_UNSYNCHRONIZATION = (byte) (1 << 7);
    /** */
    protected static final byte FLAG_EXTENDED_HEADER = (byte) (1 << 6);
    /** */
    protected static final byte FLAG_EXPERIMENTAL = (byte) (1 << 5);
}

/* */
