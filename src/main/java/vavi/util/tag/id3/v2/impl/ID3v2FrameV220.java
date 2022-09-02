// ID3v2Frame.java
// $Id: ID3v2Frame.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
//
// de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
// Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the
// Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA  02111-1307, USA.

package vavi.util.tag.id3.v2.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import vavi.util.StringUtil;
import vavi.util.tag.id3.Bytes;
import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2Exception;
import vavi.util.tag.id3.v2.ID3v2Factory;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavi.util.tag.id3.v2.ID3v2Header;


/**
 * This class contains one ID3v2 frame. (Version 2.2.0)
 *
 * Note: ID3v2 frame does not now anything about unsynchronization. That is up to
 * higher level objects (i.e. ID3v2)
 */
public class ID3v2FrameV220 implements ID3v2Frame, Serializable {
    /** jdk1.4 logger */
    private static Logger logger = Logger.getLogger(ID3v2FrameV220.class.getName());

    /**
     * Creates a new ID3v2 frame.
     *
     * @param id Frame id
     * @param content Frame content. Must not be unsynchronized!
     * @param compression_type Use contant from this class:
     *   <code>ID3v2Frame.NO_COMPRESSION</code>: <code>content</code> is not compressed and should not
     *   be compressed.
     *   <code>ID3v2Frame.IS_COMPRESSED</code>: <code>content</code> is already compressed
     *   <code>ID3v2Frame.DO_COMPRESS</code>: <code>content</code> is not compressed, but should be
     *   Compression can also be switched on/off with <code>setCompression</code>
     * @throws ID3v2Exception If content is compressed and decompression fails
     */
    public ID3v2FrameV220(String id, byte[] content, byte compression_type) throws ID3v2Exception {
        this.id = id;
        this.content = content;
        this.compression = compression_type == DO_COMPRESS || compression_type == IS_COMPRESSED;

        if (compression_type == DO_COMPRESS) {
            // compress content
            decompressed_length = this.content.length;
            compressContent();
        } else if (compression_type == IS_COMPRESSED) {
            // decompress content
            compressed_content = content;
            decompressContent();
            decompressed_length = this.content.length;
        } else {
            // no compression
            decompressed_length = this.content.length;
            compressed_content = this.content;
        }
    }

    /**
     * Creates a new ID3v2 frame from a stream.
     * Stream position must be set to first byte of frame.
     * Note: Encryption/Deencryption is not supported, so content of
     *       encrypted frames will be returned encrypted. It is up to
     *       the higher level routines to decompress it.
     * Note^2: Compression/decompression supports only GZIP.
     *
     * @param in Stream to read from
     * @throws ID3v2Exception If input is compressed and decompression fails
     * @throws IOException If I/O error occurs
     */
    public ID3v2FrameV220(InputStream in, ID3v2Header header) throws IOException, ID3v2Exception {
        // decode id
        byte[] head = new byte[6];
        DataInputStream dis = new DataInputStream(in);
        dis.readFully(head, 0, 3);
        this.id = new String(head, 0, 3);

        if (head[0] == 0) { // 0 is padding
            this.id = ID_INVALID;
            return;
        }

        // decode size (needed to read content)
        dis.readFully(head, 3, 3);
        int length = (int) (new Bytes(head, 3, 3)).getValue();
        if (!ids.containsValue(id)) {
            if (id.matches("[A-Z0-9]{3}")) {
logger.info("unknown id: " + id + ", " + length);
            } else {
logger.warning("maybe crush: " + StringUtil.getDump(head, 4) + ", " + length);
            }
            dis.skipBytes(length);
            return;
        }

        this.compression = (header.getFlag() & ID3v2HeaderV220.FLAG_COMPRESS) > 0;
//logger.info("compression: " + compression);

        // FIXME axel.wernicke@gmx.de end
        //// read content
        content = new byte[length];
        dis.readFully(content);

        // decompress if necessary
        if (compression) {
            compressed_content = new byte[content.length];
            System.arraycopy(content, 0, compressed_content, 0, content.length);

            decompressContent();
        }
    }

    // compression type

    /** */
    public final static byte NO_COMPRESSION = 0;

    /** */
    public final static byte IS_COMPRESSED = 1;

    /** */
    public final static byte DO_COMPRESS = 2;

    // IDs

    /** */
    public final static String ID_INVALID = null;

    /** */
    public String getID() {
        return id;
    }

    /** */
    public void setID(String id) {
        this.id = id;
    }

    /** */
    public boolean getCompression() {
        return compression;
    }

    /** */
    public void setCompression(boolean compression) {
        this.compression = compression;
    }

    /**
     * Calculates the number of bytes necessary to store a byte representation
     * of this frame
     */
    public int getLength() {
        // header: frame id (3 bytes), size (3 bytes) + content length
        int length = 6;

        // content
        if (compression) {
            length += compressed_content.length;
        } else {
            // bugfix axel.wernicke@gmx.de begin
            // sometimes a null pointer exception occured in here ... specially if the file was created by iTunes ??
            if (content != null) {
                length += content.length;
            }

            // bugfix axel.wernicke@gmx.de end
        }

        return length;
    }

    /**
     * Returns content (decompressed)
     */
    public FrameContent getContent(String key) {
        return ID3v2Factory.createFrameContent(key, content);
    }

    /**
     * Returns an array of bytes representing this frame
     */
    public byte[] getBytes() {
        // get length, this is used more than once, so store it
        int length = getLength();
        byte[] ret = new byte[length];

        // write header
        // write id
        for (int i = 0; i < 3; i++) {
            if (id.length() < (i - 1)) {
                // this should not happen, all ids are 4 chars long...
                ret[i] = 0;
            } else {
                ret[i] = (byte) id.charAt(i);
            }
        }

        // write size
        byte[] size_byte = (new Bytes(length - 6, 3)).getBytes();
        System.arraycopy(size_byte, 0, ret, 3, 3);

        short content_offset = 6; // first byte used for content

        // content
        if (compression) {
            compressContent();
            System.arraycopy(compressed_content, 0, ret, content_offset, compressed_content.length);
        } else {
            System.arraycopy(content, 0, ret, content_offset, content.length);
        }

        return ret;
    }

    /** */
    private String id;
    /** */
    private int decompressed_length;
    /** */
    private boolean compression;
    /** */
    private boolean uses_unsynch;
    /** decompressed */
    private byte[] content;
    /** compressed */
    private byte[] compressed_content;

    /**
     * Compresses content
     */
    private void compressContent() {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gout = new GZIPOutputStream(bout);

            // write (compress)
            gout.write(content, 0, content.length);
            gout.close();

            // write into compressed_content
            compressed_content = bout.toByteArray();

            // did compression really reduce size?
            if (content.length <= compressed_content.length) {
                compression = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // how should this happen? We are writing to memory...
        }
    }

    /**
     * Decompresses content
     */
    private void decompressContent() throws ID3v2Exception {
        ByteArrayInputStream bin = new ByteArrayInputStream(compressed_content);
logger.info("\n" + StringUtil.getDump(bin, 0, 256));
        try {
            GZIPInputStream gin = new GZIPInputStream(bin);

            // GZIPInputStream does not tell the array size needed to store the
            // decompressed array, so we write it byte by byte into a ByteArrayOutputStream
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            int res = 0;
            while ((res = gin.read()) != -1) {
                bout.write(res);
            }

            content = bout.toByteArray();
        } catch (IOException e) {
e.printStackTrace();
            throw new ID3v2Exception("decompession", e);
        }
    }

    /** */
    public boolean isFrameOf(String key) {
        return id.equals(ids.getProperty(key));
    }

    /** */
    private static final Properties ids = new Properties(); 

    /* */
    static {
        try {
            ids.load(ID3v2FrameV220.class.getResourceAsStream("/vavi/util/tag/id3/v2/impl/v220.properties"));
        } catch (IOException e) {
e.printStackTrace(System.err);
        }
    }
}

/* */
