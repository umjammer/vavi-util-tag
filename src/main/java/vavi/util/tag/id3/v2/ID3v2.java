// ID3v2.java
//
// $Id: ID3v2.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
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

package vavi.util.tag.id3.v2;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import vavi.util.tag.TagException;
import vavi.util.tag.id3.ID3Tag;
import vavi.util.tag.id3.v2.impl.ID3v2HeaderV230;
import vavix.util.FileUtil;


/**
 * Instances of this class contains an ID3v2 tag
 * <p>
 * Notes:
 * <ol>
 * <li> There are two ways of detecting the size of padding used:
 *    <p>
 *    a) The "Size of padding" field in the extended header<br>
 *    b) Detecting all frames and substracting the tag's actual
 *       length from its' length in the header.<br>
 *    Method a) is used in preference, so if a wrong padding
 *    size is stated in the extended header, all bad things
 *    may happen.
 * </li>
 * <li> Although the ID3v2 informal standard does not state it,
 *    this class will only detect an ID3v2 tag if is starts at
 *    the first byte of a file.
 * </li>
 * <li> There is no direct access to the header and extended header.
 *    Both are read, created and written internally.
 * </li>
 * </ol>
 */
public class ID3v2 implements ID3Tag, Serializable {

    /** jdk1.4 logger */
    private static final Logger logger = Logger.getLogger(ID3v2.class.getName());

    /**
     * Provides access to ID3v2 tag. When used with an InputStream, no writes are possible
     * (<code>update</code> will fail with an <code>IOException</code>, so make sure you
     * just read.
     *
     * @param in Input stream to read from. Stream position must be set to beginning of file
     *        (i.e. position of ID3v2 tag).
     * @throws IOException If I/O errors occur
     * @throws ID3v2Exception If file contains an IDv2 tag of higher version than
     *            <code>VERSION</code>.<code>REVISION</code>
     * @throws ID3v2Exception If file contains CRC and this differs from CRC calculated
     *            from the frames
     * @throws ID3v2Exception If a decompression error occurred while decompressing
     *            a compressed frame
     */
    public ID3v2(InputStream in) throws IOException, ID3v2Exception {
        this.file = null;

        // open file and read tag (if present)
        header = ID3v2Factory.readHeaderFrom(in);

        use_unsynchronization = header.unsynch;

        // tag present
        if (header.getVersion() > 2) {
            if (((ID3v2HeaderV230) header).hasExtendedHeader()) {
                readExtendedHeader(in);
            }
        }

        readFrames(in);

        in.close();
        // begin fix by axel.wernicke@gmx.de 03/02/02
        in = null;
        // end fix
        is_changed = false;
    }

    /**
     * Provides access to <code>file</code>'s ID3v2 tag
     *
     * @param file File to access
     * @throws IOException If I/O errors occur
     * @throws ID3v2Exception If file contains an IDv2 tag of higher version than
     *            <code>VERSION</code>.<code>REVISION</code>
     * @throws ID3v2Exception If file contains CRC and this differs from CRC calculated
     *            from the frames
     * @throws ID3v2Exception If a decompression error occured while decompressing
     *            a compressed frame
     */
    public ID3v2(File file) throws IOException, ID3v2Exception {
        this(Files.newInputStream(file.toPath()));
        this.file = file;
    }

    /**
     * This method undoes the effect of the unsynchronization scheme
     * by replacing $FF $00 by $FF
     *
     * @param in Array of bytes to be "synchronized"
     * @return Changed array or null if no "synchronization" was necessary
     */
    public static byte[] synchronize(byte[] in) {
        boolean did_synch = false;
        byte[] out = new byte[in.length];
        int outpos = 0; // next position to write to

        for (int i = 0; i < in.length; i++) {
            // Check every byte if it is $FF
            if (in[i] == (byte) 255) {
                // synchronize if next byte is $00
                if (in[i + 1] == 0) {
                    did_synch = true;
                    out[outpos++] = (byte) 255;
                    i++;
                } else {
                    out[outpos++] = (byte) 255;
                }
            } else {
                out[outpos++] = in[i];
            }
        }

        // make out smaller if necessary
        if (outpos != in.length) {
            // removed one or more bytes
            byte[] tmp = new byte[outpos];
            System.arraycopy(out, 0, tmp, 0, outpos);
            out = tmp;
        }

        if (did_synch) {
            return out;
        } else {
            return null;
        }
    }

    /**
     * Unsynchronizes an array of bytes by replacing $FF 00 with
     * $FF 00 00 and %11111111 111xxxxx with
     * %11111111 00000000 111xxxxx.
     *
     * @param in Array of bytes to be "unsynchronized"
     * @return Changed array or null if no change was necessary
     */
    public static byte[] unsynchronize(byte[] in) {
        byte[] out = new byte[in.length];
        int outpos = 0; // next position to write to
        boolean did_unsync = false;

        for (int i = 0; i < in.length; i++) {
            // Check every byte in in if it is $FF
            if (in[i] == -1) {
                // yes, perhaps we must unsynchronize
                // axel.wernicke@gmx.de 030126 TODO sometimes we get an array out of bound exception in here ...
                // axel.wernicke@gmx.de 030129 TODO much worse: this destroys xFExFF mark for unicode :(
                if (((in[i + 1] & 0xff) >= 0xe0) || (in[i + 1] == 0)) {
                    // next byte is %111xxxxx or %00000000,
                    // we must unsynchronize
                    // first, enlarge out by one element
                    byte[] tmp = new byte[out.length + 1];
                    System.arraycopy(out, 0, tmp, 0, outpos);
                    out = tmp;
                    tmp = null;
                    out[outpos++] = -1;
                    out[outpos++] = 0;
                    out[outpos++] = in[i + 1];

                    // skip next byte, we have already written it
                    i++;

                    did_unsync = true;
                } else {
                    // no unsynchronization necessary
                    out[outpos++] = in[i];
                }
            } else {
                // no unsynchronization necessary
                out[outpos++] = in[i];
            }
        }

        if (did_unsync) {
            // we did some unsynchronization
            return out;
        } else {
            return null;
        }
    }

    /**
     * Enables or disables use of padding (enabled by default)
     *
     * @param use_padding True if padding should be used
     */
    public void setUsePadding(boolean use_padding) {
        if (this.use_padding != use_padding) {
            is_changed = true;
            this.use_padding = use_padding;
        }
    }

    /**
     * Gets padding usage
     *
     * @return True if padding is used
     */
    public boolean getUsePadding() {
        return use_padding;
    }

    /**
     * Enables / disables use of CRC
     *
     * @param use_crc True if CRC should be used
     */
    public void setUseCRC(boolean use_crc) {
        if (this.use_crc != use_crc) {
            is_changed = true;
            this.use_crc = use_crc;
        }
    }

    /**
     * @return True if CRC is used
     */
    public boolean getUseCRC() {
        return use_crc;
    }

    /**
     * Enables / disables use of unsynchronization
     *
     * @param use_unsynch True if unsynchronization should be used
     */
    public void setUseUnsynchronization(boolean use_unsynch) {
        if (this.use_unsynchronization != use_unsynch) {
            is_changed = true;
            this.use_unsynchronization = use_unsynch;
        }
    }

    /**
     * @return True if unsynchronization should be used
     */
    public boolean getUseUnsynchronization() {
        return use_unsynchronization;
    }

    /**
     * Return all frame with ID <code>id</code>
     *
     * @param key key for Frame ID
     * @return Requested frames
     * @throws ID3v2Exception If file does not contain ID3v2Tag
     * @throws ID3v2Exception If file does not contain requested ID3v2 frame
     */
    private List<ID3v2Frame> getFrame(String key) throws ID3v2Exception {
        if (frames == null) {
            throw new ID3v2Exception("there is no frame");
        }

        List<ID3v2Frame> results = new ArrayList<>();
        for (ID3v2Frame frame : frames) {
            if (frame.isFrameOf(key)) {
                results.add(frame);
            }
        }

        if (results.isEmpty()) {
            // no frame found
//if (key.equals("Track")) {
// System.err.println("---- " + key + ", " + header.getVersion() + "." + header.getRevision());
// for (ID3v2Frame frame : frames) {
//  System.err.println("frame: " + frame.getID());
// }
// System.err.println("----");
//}
            throw new ID3v2Exception("no such frame: " + key);
        } else {
            return results;
        }
    }

    /**
     * Add a frame
     *
     * @param frame Frame to add
     */
    public void addFrame(ID3v2Frame frame) {
        if (frames == null) {
            frames = new ArrayList<>();
        }

        frames.add(frame);
        is_changed = true;
    }

    /**
     * Remove a frame.
     *
     * @param frame Frame to remove
     * @throws ID3v2Exception If file does not contain ID3v2Tag
     * @throws ID3v2Exception If file does not contain requested ID3v2 frame
     */
    public void removeFrame(ID3v2Frame frame) throws ID3v2Exception {
        if (frames == null) {
            throw new ID3v2Exception("there is no frame");
        }

        if (!frames.remove(frame)) {
            throw new ID3v2Exception("no such frame: " + frame.getID());
        }
        is_changed = true;
    }

    /**
     * Remove all frames with a given id.
     *
     * @param key key for ID of frames to remove
     * @throws ID3v2Exception If file does not contain ID3v2Tag
     * @throws ID3v2Exception If file does not contain requested ID3v2 frame
     */
    public void removeFrame(String key) throws ID3v2Exception {
        if (frames == null) {
            throw new ID3v2Exception("there is no frame");
        }

        boolean found = false; // will be true if at least one frame was found
        for (ID3v2Frame frame : frames) {
            if (frame.isFrameOf(key)) {
                frames.remove(frame);
                found = true;
            }
        }

        if (!found) {
            throw new ID3v2Exception("no such frame: " + key);
        }
        is_changed = true;
    }

    /**
     * Remove a spefic frames with a given id. A number is given to identify the frame
     * if more than one frame exists
     *
     * @param key key for ID of frames to remove
     * @param number Number of frame to remove (the first frame gets number 0)
     * @throws ID3v2Exception If file does not contain ID3v2Tag
     * @throws ID3v2Exception If file does not contain requested ID3v2 frame
     */
    public void removeFrame(String key, int number) throws ID3v2Exception {
        if (frames == null) {
            throw new ID3v2Exception("there is no frame");
        }

        int count = 0; // Number of frames with id found so far
        boolean removed = false; // will be true if at least frame was removed
        for (ID3v2Frame frame : frames) {
            if (frame.isFrameOf(key)) {
                if (count == number) {
                    frames.remove(frame);
                    removed = true;
                } else {
                    count++;
                }
            }
        }

        if (!removed) {
            throw new ID3v2Exception("no such frame: " + key);
        }
        is_changed = true;
    }

    /**
     * Write changes to file
     *
     * @throws IOException If an I/O error occurs
     */
    public void update() throws IOException {
        // don't write changes if not necessary
        if (!is_changed) {
logger.fine("not changed");
            return;
        }

        // create array of bytes from id3v2 frames
        byte[] bframes = convertFramesToArrayOfBytes();

        // unsynchronize frames if necessary
        boolean uses_unsynchronization = false;
        if (use_unsynchronization) {
            byte[] uns_frames = unsynchronize(bframes);
            if (uns_frames != null) {
                uses_unsynchronization = true;
                bframes = uns_frames;
            }
        }

        // length of header + tags + padding in bytes
        int length_file = (header != null) ? (header.getTagSize() + 10) : 0;

        // disable extended headers since they cause some trouble...
        boolean use_extended_header = false;

        // calculate new length of id3v2 header, extended header and tags - without padding !
        int new_length = (use_extended_header) ? ((new ID3v2ExtendedHeader(use_crc, 0, 0)).getBytes().length + bframes.length + 10) : (bframes.length + 10);

        // check if we can update the file inplace therefore we need
        // if more space is needed than provided or no padding should be used and
        // lengths do not mach exactly, create a temporary file
        boolean updateInplace = !((header == null) || ((header != null) && (new_length > length_file)) || ((!use_padding) && (new_length != length_file)));

        // prepare crc checksum if crc is used...
        int crc = 0;
        if (use_crc) {
            CRC32 crc_calculator = new CRC32();
            crc_calculator.update(bframes);
            crc = (int) crc_calculator.getValue();
        }

        // calculate padding size
        long padding = 0;
        if (updateInplace) {
            // we're writing to old file, fill remainder with padding
            padding = length_file - new_length;
        } else {
            // if we're writing to new file, use enough padding to make resulting file size a multiple of 2048 bytes
            // calculate resulting file size
            long res_file_size = file.length() - length_file + new_length;
            padding = ((long) (Math.ceil(res_file_size / 2048.0) * 2048) + 2048) - res_file_size;
        }

        //
        // ----------------- create new extended ID3V2 HEADER ---------------------
        //
        ID3v2ExtendedHeader new_ext_header = use_extended_header ? new ID3v2ExtendedHeader(use_crc, crc, (int) padding) : null;

        byte[] bext_header = use_extended_header ? new_ext_header.getBytes() : new byte[0];

        // unsynchronize extended header if necessary
        if (use_unsynchronization) {
            byte[] uns_ext_header = unsynchronize(bext_header);
            if (uns_ext_header != null) {
                uses_unsynchronization = true;
                bext_header = uns_ext_header;
            }
        }

        //
        // ----------------- create new ID3V2 HEADER ---------------------
        //
        ID3v2HeaderV230 new_header = new ID3v2HeaderV230(uses_unsynchronization, use_extended_header, false, bext_header.length + bframes.length + (int) padding);
        byte[] bheader = new_header.getBytes();

        //
        // ----------------- write ID3V2 HEADER, EXTENDED HEADER, TAG FRAMES AND PADDING ---------------------
        //
        // open file to write data to ( can be temp or original file )
        File write_to = updateInplace ? file : File.createTempFile("ID3", null, file);
        java.io.RandomAccessFile out = new java.io.RandomAccessFile(write_to, "rw");
        out.write(bheader);
        out.write(bext_header);
//logger.info("\n" + StringUtil.getDump(bframes, 512));
        out.write(bframes);

        // write padding
        if (use_padding) {
            for (int i = 0; i < padding; i++) {
                out.write(0);
            }
        }

        // write rest of file if we are using a temporary file
        if (!updateInplace) {
            BufferedInputStream copy_out = new BufferedInputStream(Files.newInputStream(file.toPath()));

            // go to first byte after ID3v2 tag
            if (header != null) {
                copy_out.skipNBytes(length_file - 1);
            }

            int localBufferSize = 32768;
            byte[] localBuffer = new byte[localBufferSize]; // init local buffer

            // copy as long as we get a full chunk
            long readBytes = copy_out.read(localBuffer);
            while (readBytes == localBufferSize) {
                out.write(localBuffer);
                readBytes = copy_out.read(localBuffer);
            }

            // write last couple of bytes
            for (int i = 0; i < readBytes; i++) {
                out.write(localBuffer[i]);
            }

            // close source stream
            copy_out.close();

            // temp file: rename file to original filename
            // if temp file and file are in the same directory, we can rename
            boolean renamed = write_to.renameTo(file);
            if (!renamed) {
                // hell, we must copy
                // pri.nightmare.utils.File.copy(write_to.getAbsolutePath(), file.getAbsolutePath());
                FileUtil.copy(file, write_to);

                // delete now or later
                if (!write_to.delete()) {
                    write_to.deleteOnExit();
                }
            }
        }

        // close destination stream
        out.close();

        // update id3v2 object
        header = new_header;
        extended_header = new_ext_header;
        is_changed = false;
    }

    public int getVersion() {
        return 200 + header.getVersion() * 10 + header.getRevision();
    }

    /** */
    private File file;
    /** */
    private ID3v2Header header;
    /** */
    private ID3v2ExtendedHeader extended_header;
    /** */
    private List<ID3v2Frame> frames;
    /** */
    private boolean is_changed = false;
    /** */
    private boolean use_padding = true;
    /** */
    private boolean use_crc = true;
    /** */
    private boolean use_unsynchronization = false;

    /**
     * Read extended ID3v2 header from input stream <tt>in</tt>
     *
     * @param in Input stream to read from
     */
    private void readExtendedHeader(InputStream in) throws IOException {
        // in file pointer must be at correct position (header
        // has just been read)
        extended_header = new ID3v2ExtendedHeader(in);
    }

    /**
     * Read ID3v2 frames from stream <tt>in</tt>
     * Stream position must be set to beginning of frames
     *
     * @param in Stream to read from
     */
    private void readFrames(InputStream in) throws IOException, ID3v2Exception {
        // steps to read frames:
        // 1) Read all frames as bytes (don't include padding if size of padding is
        //                              known, i.e. ext. header exists)
        // 2) If CRC is present, make CRC check on frames
        // 3) Convert bytes to ID3v2Frames

        // read all frames as bytes
        // calculate number of bytes to be read
        int bytes_to_read;
        if (extended_header != null) {
            // ext. header exists
            bytes_to_read = header.getTagSize() - (extended_header.getSize() + 4) - extended_header.getPaddingSize();

            // FIXME axel.wernicke@gmx.de begin
            // check plausibility - failures can be occure, if there is a
            // extended header flagged but not contained in the file
            if (bytes_to_read < 0) {
                bytes_to_read = header.getTagSize();
            }

            // FIXME axel.wernicke@gmx.de end
        } else {
            // no ext. header, include padding
            bytes_to_read = header.getTagSize();
        }

        // read bytes
        byte[] unsynch_frames_as_byte = new byte[bytes_to_read];
        int l = 0;
        while (l < bytes_to_read) {
            int r = in.read(unsynch_frames_as_byte, l, bytes_to_read - l);
            if (r < -1) {
                throw new EOFException();
            }
            l += r;
        }

        byte[] frames_as_byte;
        if (header.getUnsynchronization()) {
            // undo effects of unsynchronization
            frames_as_byte = synchronize(unsynch_frames_as_byte);
            if (frames_as_byte == null) {
                frames_as_byte = unsynch_frames_as_byte;
            }
        } else {
            frames_as_byte = unsynch_frames_as_byte;
        }

        // CRC check
        if ((extended_header != null) && extended_header.hasCRC()) {
            // make CRC check
            // calculate crc of read frames (because extended header exists,
            // they contain no padding)
            java.util.zip.CRC32 crc_calculator = new java.util.zip.CRC32();
            crc_calculator.update(frames_as_byte);
            int crc = (int) crc_calculator.getValue();

            if (crc != (int) extended_header.getCRC()) {
                // crc mismatch
                //throw new ID3v2WrongCRCException();
            }
        }

        // Convert bytes to ID3v2Frames
        frames = new ArrayList<>();

        ByteArrayInputStream bis = new ByteArrayInputStream(frames_as_byte);
//logger.info("frames\n" + StringUtil.getDump(frames_as_byte));

        // read frames as long as there are bytes and we are not reading from padding
        // (indicated by invalid frame id)
        while (bis.available() > 8) {
            ID3v2Frame frame = ID3v2Factory.readFrameFrom(bis, header);
            if (Objects.equals(frame.getID(), ID3v2Frame.ID_INVALID)) {
                // reached end of frames
logger.fine("invalid id found");
                break;
            } else {
logger.fine(frame.getID() + "\n" + frame.getContent(frame.getID()));
                frames.add(frame);
            }
        }
logger.fine("skip: " + bis.available());
        bis.skipNBytes(bis.available());
    }

    /**
     * Convert all frames to an array of bytes
     */
    private byte[] convertFramesToArrayOfBytes() {

        ByteArrayOutputStream out = new ByteArrayOutputStream(500);

        for (ID3v2Frame frame : frames) {
            // fix begin by axel.wernicke@gmx.de 030126
            // sometimes we get an exception from frame.getBytes - this is caused
            // by empty or misencoded frames
            byte[] frame_in_bytes = frame.getBytes();
            out.write(frame_in_bytes, 0, frame_in_bytes.length);
            // fix end
        }

        return out.toByteArray();
    }

    /**
     * @param key key for frame id, e.g "Comments", "Album"
     */
    public Object getTag(String key) throws ID3v2Exception {
        List<ID3v2Frame> frames = getFrame(key);
// TODO multiple comment frames
if ("Comments".equals(key) && frames.size() > 1) {
 logger.info("key: " + key + ", " + frames.size());
 int c = 0;
 for (ID3v2Frame frame : frames) {
  logger.info(" frame[" + c++ + "]: " + frame.getContent(key).getClass().getName());
 }
}
        ID3v2Frame frame = frames.get(0);
        FrameContent content = frame.getContent(frame.getID());
//logger.fine("content: " + content.getClass().getSimpleName());
        Object object = content.getContent();
        return object;
    }

    /** */
    public void setTag(String key, Object content) throws ID3v2Exception {
        try {
            FrameContent frameContent = getFrame(key).get(0).getContent(key);
            frameContent.setContent(content); // TODO override each content!
        } catch (ID3v2Exception e) {
            e.printStackTrace(System.err);
            addFrame(ID3v2Factory.createFrame(key, ID3v2Factory.createFrameContent(key, content)));
        }
    }

    /* */
    public Iterator<?> tags() throws TagException {
        List<ID3v2Frame> results = new ArrayList<>(frames);
        return results.iterator();
    }
}

/* */
