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
import java.util.Enumeration;
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


/**
 * This class contains one ID3v2 frame. (Version 2.3.0)
 *
 * Note: ID3v2 frame does not now anything about unsynchronization. That is up to
 * higher level objects (i.e. ID3v2)
 */
public class ID3v2FrameV230 implements ID3v2Frame, Serializable {
    /** jdk1.4 logger */
    private static Logger logger = Logger.getLogger(ID3v2FrameV230.class.getName());

    /**
     * Creates a new ID3v2 frame.
     *
     * @param key key for frame id
     * @param content Frame content. Must not be unsynchronized!
     * @param tag_alter_preservation True if frame should be discarded if frame id
     *        is unknown to software and tag is altered
     * @param file_alter_preservation Same as <code>tag_alter_preservation</code>, but applies if
     *        file (excluding tag) is altered
     * @param read_only True if frame should not be changed
     * @param compression_type Use contant from this class:
     *   <code>ID3v2Frame.NO_COMPRESSION</code>: <code>content</code> is not compressed and should not
     *   be compressed.
     *   <code>ID3v2Frame.IS_COMPRESSED</code>: <code>content</code> is already compressed
     *   <code>ID3v2Frame.DO_COMPRESS</code>: <code>content</code> is not compressed, but should be
     *   Compression can also be switched on/off with <code>setCompression</code>
     * @param encryption_id Encryption method or 0 if not encrypted (not completely supported,
     *        encryption must be done externally)
     * @param group Group of frames this frame belongs to or 0 if frame does not belong to any group
     * @throws ID3v2Exception If content is compressed and decompression fails
     */
    public ID3v2FrameV230(String key, byte[] content, boolean tag_alter_preservation, boolean file_alter_preservation, boolean read_only, byte compression_type, byte encryption_id, byte group) throws ID3v2Exception {
        this.id = ids.getProperty(key);
        if (id == null) {
            throw new IllegalArgumentException("unknown key: " + key);
        }
        this.content = content;
        this.tag_alter_preservation = tag_alter_preservation;
        this.file_alter_preservation = file_alter_preservation;
        this.read_only = read_only;
        this.compression = compression_type == DO_COMPRESS || compression_type == IS_COMPRESSED;
        this.encryption_id = encryption_id;
        this.group = group;

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
    public ID3v2FrameV230(InputStream in) throws IOException, ID3v2Exception {
        // decode id
        byte[] head = new byte[10];
        DataInputStream dis = new DataInputStream(in);
        dis.readFully(head, 0, 4);
        this.id = new String(head, 0, 4);

        if (head[0] == 0) { // 0 is padding
            this.id = ID_INVALID;
            return;
        }

        // decode size (needed to read content)
        dis.readFully(head, 4, 4);
        int length = (int) (new Bytes(head, 4, 4)).getValue();
//logger.info("id: " + id + ": " + StringUtil.getDump(head, 4) + ", length: " + length);
        if (!ids.containsValue(id)) {
            if (id.matches("[A-Z0-9]{4}")) {
logger.fine("unknown id: " + id + ", " + length);
                content = new byte[2 + length]; // TODO more smart
                dis.readFully(content);
            } else {
logger.warning("maybe crush: " + StringUtil.getDump(head, 4) + "[" + id + "], " + length);
                dis.skipBytes(2 + length);
            }
            return;
        }

        // decode flags
        dis.readFully(head, 8, 2);
        if (((head[8] & 0xff) & FLAG_TAG_ALTER_PRESERVATION) > 0) {
            tag_alter_preservation = true;
        }
        if (((head[8] & 0xff) & FLAG_FILE_ALTER_PRESERVATION) > 0) {
            file_alter_preservation = true;
        }
        if (((head[8] & 0xff) & FLAG_READ_ONLY) > 0) {
            read_only = true;
        }
        if (((head[9] & 0xff) & FLAG_COMPRESSION) > 0) {
            compression = true;
        }
        boolean encryption = false;
        if (((head[9] & 0xff) & FLAG_ENCRYPTION) > 0) {
            encryption = true;
        }
        boolean grouping = false;
        if (((head[9] & 0xff) & FLAG_GROUPING) > 0) {
            grouping = true;
        }
//logger.info("compression: " + compression);
//logger.info("encryption: " + encryption);
//logger.info("grouping: " + grouping);

        // additional bytes if present
        if (compression) {
            // read decompressed size
            byte[] decomp_byte = new byte[4];
            dis.readFully(decomp_byte);
            decompressed_length = (int) (new Bytes(decomp_byte)).getValue();

            // substract 4 bytes from length to get actual content length
            length -= 4;
        }

        if (encryption) {
            // read encryption type
            encryption_id = dis.readByte();
            length--;
        }

        if (grouping) {
            // read group id
            group = dis.readByte();

            // substract 1 byte from length to get actual content length
            length--;
        } else {
            group = 0;
        }

        // FIXME axel.wernicke@gmx.de end
        // read content
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

    public final static byte NO_COMPRESSION = 0;
    public final static byte IS_COMPRESSED = 1;
    public final static byte DO_COMPRESS = 2;

    /** */
    public String getID() {
        return id;
    }

    /** */
    public void setID(String id) {
        this.id = id;
    }

    /** */
    public boolean getTagAlterPreservation() {
        return tag_alter_preservation;
    }

    /** */
    public void setTagAlterPreservation(boolean tag_alter_preservation) {
        this.tag_alter_preservation = tag_alter_preservation;
    }

    /** */
    public boolean getFileAlterPreservation() {
        return file_alter_preservation;
    }

    /** */
    public void setFileAlterPreservation(boolean file_alter_preservation) {
        this.file_alter_preservation = file_alter_preservation;
    }

    /** */
    public boolean getReadOnly() {
        return read_only;
    }

    /** */
    public void setReadOnly(boolean read_only) {
        this.read_only = read_only;
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
     * @return Encryption ID or 0 if not encrypted
     */
    public byte getEncryptionID() {
        return encryption_id;
    }

    /** */
    public void setEncryption(byte encryption_id) {
        this.encryption_id = encryption_id;
    }

    /** */
    public byte getGroup() {
        return group;
    }

    /** */
    public void setGroup(byte group) {
        this.group = group;
    }

    /**
     * Calculates the number of bytes necessary to store a byte representation
     * of this frame
     */
    public int getLength() {
        // header: frame id (4 bytes), size (4 bytes), flags (2 bytes) + content length
        int length = 10;

        // if compression is set, add 4 bytes for decompressed size
        if (compression) {
            length += 4;
        }

        // if encryption is set, add one byte for encryption id
        if (encryption_id != 0) {
            length++;
        }

        // if group is set, add one byte for group identifier
        if (group != 0) {
            length++;
        }

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
     *
     * @param atom four letters name
     */
    public FrameContent getContent(String atom) {
        Enumeration<?> e = ids.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = ids.getProperty(key);

            if (value.equals(atom)) {
                return ID3v2Factory.createFrameContent(key, content);
            }
        }

logger.warning("no key for: " + atom);
        return ID3v2Factory.createFrameContent("Unknown", content);
    }

    /**
     * Returns content (decompressed)
     */
    public FrameContent getContent() {
        Enumeration<?> e = ids.keys();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = ids.getProperty(key);
//logger.info(getID() + ": " + key + ", " + value);
            if (value.equals(getID())) {
                return ID3v2Factory.createFrameContent(key, content);
            }
        }
logger.warning("no key for: " + getID());
        return ID3v2Factory.createFrameContent("Unknown", content);
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
        for (int i = 0; i < 4; i++) {
            if (id.length() < (i - 1)) {
                // this should not happen, all ids are 4 chars long...
                ret[i] = 0;
            } else {
                ret[i] = (byte) id.charAt(i);
            }
        }

        // write size
        byte[] size_byte = new Bytes(length - 10, 4).getBytes();
        System.arraycopy(size_byte, 0, ret, 4, 4);

        // write flags
        byte flag1 = 0;
        if (tag_alter_preservation) {
            flag1 = (byte) (flag1 | FLAG_TAG_ALTER_PRESERVATION);
        }
        if (file_alter_preservation) {
            flag1 += (byte) (flag1 | FLAG_FILE_ALTER_PRESERVATION);
        }
        if (read_only) {
            flag1 += (byte) (flag1 | FLAG_READ_ONLY);
        }
        ret[8] = flag1;

        byte flag2 = 0;
        if (compression) {
            flag2 += (byte) (flag2 | FLAG_COMPRESSION);
        }
        if (encryption_id != 0) {
            flag2 += (byte) (flag2 | FLAG_ENCRYPTION);
        }
        if (group > 0) {
            flag2 += (byte) (flag2 | FLAG_GROUPING);
        }
        ret[9] = flag2;

        short content_offset = 10; // first byte used for content

        // decompressed size, if compressed
        if (compression) {
            byte[] decomp_byte = (new Bytes(length, 4)).getBytes();
            System.arraycopy(decomp_byte, 0, ret, content_offset, 4);

            content_offset += 4;
        }

        // encryption id if set
        if (encryption_id != 0) {
            ret[content_offset] = encryption_id;
            content_offset++;
        }

        // group id if set
        if (group > 0) {
            ret[content_offset] = group;
            content_offset++;
        }

        // content
        if (compression) {
            compressContent();
            System.arraycopy(compressed_content, 0, ret, content_offset, compressed_content.length);
        } else {
//logger.info(getID() + ": " + content_offset + "\n" + StringUtil.getDump(content, 128));
            System.arraycopy(content, 0, ret, content_offset, content.length);
//logger.info("\n" + StringUtil.getDump(ret, 128));
        }

        return ret;
    }

    /** */
    private String id = null;
    /** */
    private boolean tag_alter_preservation = false;
    /** */
    private boolean file_alter_preservation = false;
    /** */
    private boolean read_only = false;
    /** */
    private byte encryption_id = 0;
    /** */
    private int decompressed_length = 0;
    /** */
    private boolean compression = false;
    /** */
    private byte group = 0;
    /** */
    private boolean uses_unsynch = false;
    /** decompressed */
    private byte[] content;
    /** compressed */
    private byte[] compressed_content;
    // status
    /** */
    private final static byte FLAG_TAG_ALTER_PRESERVATION = (byte) (1 << 7);
    /** */
    private final static byte FLAG_FILE_ALTER_PRESERVATION = (byte) (1 << 6);
    /** */
    private final static byte FLAG_READ_ONLY = (byte) (1 << 5);
    // format
    /** */
    private final static byte FLAG_COMPRESSION = (byte) (1 << 7);
    /** */
    private final static byte FLAG_ENCRYPTION = (byte) (1 << 6);
    /** */
    private final static byte FLAG_GROUPING = (byte) (1 << 5);

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
logger.info("\n" + StringUtil.getDump(bin, 256));
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
e.printStackTrace(System.err);
            throw new ID3v2Exception("decompession", e);
        }
    }

    /** */
    public boolean isFrameOf(String key) {
        return id.equals(ids.getProperty(key));
    }

    /** common name, four letters id */
    private static final Properties ids = new Properties();

    /* */
    static {
        try {
            ids.load(ID3v2FrameV230.class.getResourceAsStream("/vavi/util/tag/id3/v2/impl/v230.properties"));
        } catch (IOException e) {
e.printStackTrace(System.err);
        }
    }
}

/* */
