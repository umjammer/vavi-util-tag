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
public class ID3v2HeaderV220 extends ID3v2Header implements Serializable {

    /**
     * ID3v2 version
     */
    private static final byte VERSION = 2;

    /**
     * ID3v2 revision
     */
    private static final byte REVISION = 0;

    /**
     * Create a new (empty) header
     */
    public ID3v2HeaderV220() {
        this(false, 0);
    }

    /**
     * Build a ID3v2 header
     *
     * @param compress compress?
     * @param length ID3v2 tag length
     */
    public ID3v2HeaderV220(boolean compress, int length) {
        this.compress = compress;
        this.size = length;
    }

    /**
     * Creates an ID3v2 header from an input stream.
     *
     * @param in Stream to read from
     * @throws ID3v2Exception If tag has a revision higher than
     *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
     * @throws ID3v2Exception If file does not contain an ID3v2 header
     * @throws IOException If an I/O error occurs
     */
    public ID3v2HeaderV220(byte[] head) throws ID3v2Exception, IOException {
        inject(head);

        unsynch = (flag & FLAG_UNSYNCHRONIZATION) > 0;
        compress = (flag & FLAG_COMPRESS) > 0;
    }

    // Public methods

    /**
     * Is compress?
     */
    public boolean getCompress() {
        return compress;
    }

    /**
     * Set / unset compress
     *
     * @param compress True: Set compress bit
     */
    public void setCompress(boolean compress) {
        this.compress = compress;
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
        if (compress) {
            flag |= FLAG_COMPRESS;
        }
        work[5] = flag;

        // create length bytes manually ("unsynchronized")
        for (int i = 0; i < 4; i++) {
            work[i + 6] = (byte) ((size >> ((3 - i) * 7)) & 127);
        }

        return work;
    }

    /** */
    private boolean compress;

    /** */
    public static final int FLAG_UNSYNCHRONIZATION = 0x80;
    /** */
    public static final int FLAG_COMPRESS = 0x40;
}
