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


/**
 * This class contains an ID3v2 header.
 */
public class ID3v2HeaderV240 extends ID3v2HeaderV230 implements Serializable {

    /**
     * ID3v2 version
     */
    private final static byte VERSION = 4;

    /**
     * ID3v2 revision
     */
    private final static byte REVISION = 0;

    /**
     * Create a new (empty) header
     */
    public ID3v2HeaderV240() {
        this(false, false, false, false, 0);
    }

    /**
     * Build a ID3v2 header
     *
     * @param unsynch Use unsynchronization scheme?
     * @param extended_header Use extended header?
     * @param experimental Is experimental?
     * @param length ID3v2 tag length
     */
    public ID3v2HeaderV240(boolean unsynch, boolean extended_header, boolean experimental, boolean footer, int length) {
        super(unsynch, extended_header, experimental, length);
        this.footer = footer;
    }

    /**
     * Creates an ID3v2 header from an input stream.
     *
     * @param head data
     * @throws ID3v2Exception If tag has a revision higher than
     *            <code>ID3v2.VERSION</code>.<code>ID3v2.REVISION</code>
     * @throws ID3v2Exception If file does not contain an ID3v2 header
     * @throws IOException If an I/O error occurs
     */
    public ID3v2HeaderV240(byte[] head) throws ID3v2Exception, IOException {
        inject(head);

        unsynch = (flag & FLAG_UNSYNCHRONIZATION) > 0;
        extended_header = (flag & FLAG_EXTENDED_HEADER) > 0;
        experimental = (flag & FLAG_EXPERIMENTAL) > 0;
        footer = (flag & FLAG_FOOTER) > 0;
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
        if (unsynch) {
            flag |= FLAG_UNSYNCHRONIZATION;
        }
        if (extended_header) {
            flag |= FLAG_EXTENDED_HEADER;
        }
        if (experimental) {
            flag |= FLAG_EXPERIMENTAL;
        }
        if (footer) {
            flag |= FLAG_FOOTER;
        }
        work[5] = flag;

        // create length bytes manually ("unsynchronized")
        for (int i = 0; i < 4; i++) {
            work[i + 6] = (byte) ((size >> ((3 - i) * 7)) & 127);
        }

        return work;
    }

    /** */
    private boolean footer;

    /** */
    private final static byte FLAG_FOOTER = (byte) (1 << 4);
}
