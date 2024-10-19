// MP3Properties
// $Id: MP3Properties.java,v 1.2 2003/07/06 20:42:17 axelwernicke Exp $
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

package vavi.util.tag.id3;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

import vavi.util.tag.id3.v2.ID3v2Factory;
import vavi.util.tag.id3.v2.ID3v2Header;


/**
 * This class reads properties like bit rate etc. from
 * an MP3 file. Of course these properties are read only...
 * It supports ID3v2, i.e. it reliably skips
 * even files which tags do not use the unsynchronization scheme
 *
 * Illegal entries are marked with special return values, not
 * with exceptions. This enables the class to continue reading other
 * properties.
 */
class MP3Properties implements Serializable {

    /**
     * Create a new instance connected to <code>file</code>.
     * Properties are read immediately.
     *
     * @param file File to connect to
     * @throws IOException If an I/O error occurs
     * @throws ID3TagException If file does not contain at least one mp3 frame
     */
    public MP3Properties(File file) throws IOException, ID3TagException {
        readProperties(file);
    }

    /**
     * Constants for mode
     */
    public final static int MODE_STEREO = 0;
    /** */
    public final static int MODE_JOINT_STEREO = 1;
    /** */
    public final static int MODE_DUAL_CHANNEL = 2;
    /** */
    public final static int MODE_MONO = 3;

    /**
     * Constants for emphasis
     */
    public final static int EMPHASIS_ILLEGAL = 0;
    /** */
    public final static int EMPHASIS_NONE = 1;
    /** */
    public final static int EMPHASIS_5015MS = 2;
    /** */
    public final static int EMPHASIS_CCITT = 3;

    /**
     * @return MPEG level
     */
    public int getMPEGLevel() {
        return level;
    }

    /**
     * @return Layer, 0 for illegal entries
     */
    public int getLayer() {
        return layer;
    }

    /**
     * @return bitrate, 0 for illegal entries
     */
    public int getBitrate() {
        return bitrate;
    }

    /**
     * @return samplerate, 0 for illegal entries
     */
    public int getSamplerate() {
        return samplerate;
    }

    /**
     * Returns mode (mono, stereo) used in MP3 file.
     * Please use the constants MODE_XYZ.
     *
     * @return Mode
     */
    public int getMode() {
        return mode;
    }

    /**
     * Returns emphasis used in MP3 file. There are constants...
     *
     * @return emphasis
     */
    public int getEmphasis() {
        return emphasis;
    }

    /**
     * @return Protection set?
     */
    public boolean getProtection() {
        return protection;
    }

    /**
     * @return Padding set?
     */
    public boolean getPadding() {
        return padding;
    }

    /**
     * @return Private bit set?
     */
    public boolean getPrivate() {
        return privat;
    }

    /**
     * @return Copyright bit set?
     */
    public boolean getCopyright() {
        return copy;
    }

    /**
     * @return Original?
     */
    public boolean getOriginal() {
        return original;
    }

    /** Gets the vbr bit for the file
     **/
    public boolean isVBR() {
        return vbr;
    }

    /**
     * @return Length in seconds
     */
    public long getLength() {
        return length;
    }

    /** */
    protected File file;
    /** */
    protected int level;
    /** */
    protected int layer;
    /** */
    protected int bitrate;
    /** */
    protected int samplerate;
    /** */
    protected int mode;
    /** */
    protected int emphasis;
    /** */
    protected boolean protection;
    /** */
    protected boolean padding;
    /** */
    protected boolean privat;
    /** */
    protected boolean original;
    /** */
    protected boolean copy;
    /** */
    protected boolean vbr = false;
    /** */
    protected long length;

    /**
     * Read properties from MP3 file
     *
     * @param file File to read from
     * @throws IOException If an I/O error occurs
     * @throws ID3TagException If file does not contain at least one mp3 frame
     */
    protected void readProperties(File file) throws IOException, ID3TagException {
        this.file = file;
        FileInputStream in = new FileInputStream(file);

        // if no ID3v2 tag is present, we must undo the 10 bytes skip
        // done by checking
        in.mark(15);

        // skip over ID3v2 tag (if present) and get size
        int id3v2_tagsize = skipID3v2(in);

        if (id3v2_tagsize == 0) {
            // begin fix by axel.wernicke@gmx.de 02/12/26
            // if we open the file again, we should close it before ...
            // in.reset() doesn't work reliable
            if (in != null) {
                in.close();
                in = null;
            }

            // end fix
            // no tag, restart stream
            in = new FileInputStream(file);
        }

        // synchronize to next MP3 frame
        // usually, this should not be necessary
        int second = synchronize(in);
        int third = in.read();
        int fourth = in.read();

        // second, third and fourth now contain the second, third and fourth byte of
        // MP3 frame header, respectively
        // read properties
        level = convertMPEGLevel(getBit(second, 3));
        layer = convertLayer(getBit(second, 2), getBit(second, 1));
        // protection bit in invers (1: no crc)
        protection = ((getBit(second, 0)) == 0);

        bitrate = convertBitrate(getBit(third, 7), getBit(third, 6), getBit(third, 5), getBit(third, 4));
        samplerate = convertSamplerate(getBit(third, 3), getBit(third, 2));
        padding = (getBit(third, 1) == 1);
        privat = (getBit(third, 0) == 1);

        mode = convertMode(getBit(fourth, 7), getBit(fourth, 6));
        copy = (getBit(fourth, 3) == 1);
        original = (getBit(fourth, 2) == 1);
        emphasis = convertEmphasis(getBit(fourth, 1), getBit(fourth, 0));

        length = calculateLength(id3v2_tagsize);

        // begin extension by hao
        // read VBR (xing) header
        if (level == 1) { // MPEG-1
            if (mode != MODE_MONO) {
                in.skip(32);
            } else {
                in.skip(17);
            }
        } else { // MPEG-2
            if (mode != MODE_MONO) {
                in.skip(17);
            } else {
                in.skip(9);
            }
        }

        if ((in.read() == 'X') && (in.read() == 'i') && // test for 'Xing'
            (in.read() == 'n') && (in.read() == 'g') && (samplerate != 0)) { // avoid division by zero

            // 030705 axelwernicke@gmx.de added vbr detection
            // Xing means we have a vbr file
            this.vbr = true;

            // found a VBR/Xing header
            int flags = readBigEndianInt(in);

            if (getBit(flags, 0) == 1) // FRAMES_FLAG set
             {
                int frames = readBigEndianInt(in);

                // recalculate length in seconds, constant is 384 for Layer I, 1152 for Layer II and III
                length = (frames * ((layer == 1) ? 384L : 1152)) / samplerate;

                // set bitrate to average VBR bitrate
                // TODO: subtract ID3v1 tagsize, if ID3v1 tag exists
                bitrate = (int) Math.floor(((file.length() - id3v2_tagsize) * 8f) / length / 1000);
            }
            /* not needed by myPod ...
              if (getBit(flags, 1) == 1)  // BYTES_FLAG set
              {
                int bytes = readBigEndianInt(in);
              }
              if (getBit(flags, 2) == 1)  // TOC_FLAG set
              {
                // TODO: read 100 byte TOC data
              }
              if (getBit(flags, 3) == 1)  // VBR_SCALE_FLAG set
              {
                int vbr_scale = readBigEndianInt(in);
              }
            */
        }

        // end extension
        // begin fix by axel.wernicke@gmx.de 02/120/26
        in.close();
        // end fix
    }

    /**
     * If ID3v2 tag present, skips it and returns length.
     * Input stream must be set to the first byte.
     *
     * @param in Stream to read from
     * @return Size of ID3v2 tag or 0 if not present
     * @throws IOException If an I/O error occurs
     */
    protected int skipID3v2(FileInputStream in) throws IOException {
        ID3v2Header header = null;
        try {
            header = ID3v2Factory.readHeaderFrom(in);
        } catch (Exception e) {
            // no header
            return 0;
        }

        // if we reach this point, file has an ID3v2 header,
        // get size and skip input stream to first byte after
        // tag. File position is now at first byte after
        // header.
        in.skip(header.getTagSize());

        return header.getTagSize();
    }

    /**
     * Sets input stream to third byte of MP3 frame
     * header (first byte is 0xff, second is consumed in synchronizing)
     *  and returns the byte already consumed.
     *
     * @param in Stream to read from
     * @return Second byte of MP3 frame header
     * @throws IOException If an I/O error occurs
     * @throws ID3TagException If file does not contain at least one mp3 frame
     */
    protected int synchronize(FileInputStream in) throws IOException, ID3TagException {
        // skip until start of header (at least 11 bits in a row set to 1)
        boolean finished = false;

        int store = 0;

        while (!finished) {
            // read through stream until 0xff is read
            int skip = in.read();

            while ((skip != 255) && (skip != -1)) {
                skip = in.read();
            }

            if (skip == -1) {
                // End of stream reached without finding a frame
                throw new ID3TagException("no such frame");
            }

            // now next byte must to >= 224
            store = in.read();

            if (store >= 224) {
                // synchronized
                finished = true;
            } else if (store == -1) {
                // End of stream reached without finding a frame
                throw new ID3TagException("no such frame");
            } else {
                // continue search
            }
        }

        // if we reach this point, an MP3 frame has been found. If
        // file does not contain one, method has already thrown an
        // NoMP3FrameException
        return store;
    }

    // Note: All conversion methods use an int to represent a bit

    /**
     * Converts bit to MPEG level
     */
    protected int convertMPEGLevel(int in) {
        if (in == 1) {
            // 1 = MPEG-1
            return 1;
        } else {
            // 0 = MPEG-2
            return 2;
        }
    }

    /**
     * Convert 2 bits to layer
     */
    protected int convertLayer(int in1, int in2) {
        if ((in1 == 0) && (in2 == 0)) {
            // Illegal combination
            return 0;
        } else {
            // Layer is 4-in value
            return (4 - ((in1 << 1) + in2));
        }
    }

    /**
     * Convert 4 bits to bitrate
     */
    protected int convertBitrate(int in1, int in2, int in3, int in4) {
        // array used for conversion.
        // First index is the input (combined to one byte)
        // Second index is MPEG level and layer
        // (MPEG-1, layer 1; MPEG-1, layer 2; MPEG-1, layer3;
        //  MPEG-2, layer 1; MPEG-2, layer 2; MPEG-2, layer3)
        int[][] convert = {
            { 0, 0, 0, 0, 0, 0 },
            { 32, 32, 32, 32, 32, 8 },
            { 64, 48, 40, 64, 48, 16 },
            { 96, 56, 48, 96, 56, 24 },
            { 128, 64, 56, 128, 64, 32 },
            { 160, 80, 64, 160, 80, 64 },
            { 192, 96, 80, 192, 96, 80 },
            { 224, 112, 96, 224, 112, 56 },
            { 256, 128, 112, 256, 128, 64 },
            { 288, 160, 128, 288, 160, 128 },
            { 320, 192, 160, 320, 192, 160 },
            { 352, 224, 192, 352, 224, 112 },
            { 384, 256, 224, 384, 256, 128 },
            { 416, 320, 256, 416, 320, 256 },
            { 448, 384, 320, 448, 384, 320 },
            { 0, 0, 0, 0, 0, 0 }
        };

        // calculate indices
        int index1 = (in1 << 3) | (in2 << 2) | (in3 << 1) | in4;

        // MPEG level and layer must already be read
        int index2 = (((level - 1) * 3) + layer) - 1;

        // begin fix by axel.wernicke@gmx.de 02/12/26
        // sometimes you get an index out of bound exception...
        int tbitrate;
        try {
            tbitrate = convert[index1][index2];
        } catch (Exception e) {
            tbitrate = 0;
        }

        //return convert[index1][index2];
        return tbitrate;
        // end fix
    }

    /**
     * Convert 2 bits to samplerate
     */
    protected int convertSamplerate(int in1, int in2) {
        int sample = switch ((in1 << 1) | in2) {
            case 0 -> 44100;
            case 1 -> 48000;
            case 2 -> 32000;
            case 3 ->
                // begin fix by axel.wernicke@gmx.de 02/12/26
                // its legal - isn't it ??
                // Illegal
                // sample = 0;
                    16000;
            default -> 0;
            // end fix
        };

        if (level == 1) {
            return sample;
        } else {
            return sample / 2;
        }
    }

    /**
     * Convert 2 bits to mode
     */
    protected int convertMode(int in1, int in2) {
        int[] convert = {
            MODE_STEREO, MODE_JOINT_STEREO, MODE_DUAL_CHANNEL, MODE_MONO
        };
        return convert[(in1 << 1) | in2];
    }

    /**
     * Convert 2 bits to emphasize
     */
    protected int convertEmphasis(int in1, int in2) {
        int[] convert = {
            EMPHASIS_NONE, EMPHASIS_5015MS, EMPHASIS_ILLEGAL, EMPHASIS_CCITT
        };
        return convert[(in1 << 1) | in2];
    }

    /**
     * Calculate length (in seconds) of file.
     * This is pretty accurate, so it *may* differ from results by many other programs
     * (like Nightmare's ID3 Tagger ;-))
     */
    protected long calculateLength(int id3v2_tagsize) {
//        long framesize = (long)Math.ceil( 144 * bitrate / samplerate );
//
//        // header size is 4 bytes
//        // 1 byte is added if padding is set,
//        // 4 bytes checksum is added if protection is NOT set
//        int headersize = 4 + (padding ? 1 : 0) + (protection ? 0 : 4);
//
//        long filesize = file.length() - id3v2_tagsize;
//
//        // #frames = ceil(filesize / (framesize + headersize))
//        // sizeofallframes = #frames * framesize
//        // length = sizeofallframes / bitrate * 8 / 1000
//
//        return (long)(bitrate / (Math.ceil(filesize / (framesize + headersize)) * framesize) * 0.008);

        // This does not work, at least not for small bitrates
        // I have to think about it TODO
        // Instead, go for the easy solution
        // begin fix by axel.wernicke@gmx.de 02/12/26
        // sometimes you get a division by zero exception here ... caused by bitrate == 0
        long length = 0;
        try {
            length = (long) Math.floor((float) (file.length() - id3v2_tagsize) / bitrate * 0.008);
        } catch (Exception e) {
        }

        return length;
        // end fix
    }

    /**
     * Check if selected bit is set in <code>input</code>.
     * This does not really belong here, but it is needed...
     *
     * @param input Value to check
     * @param bit Bit number to check (0..7 with 7 MSB)
     * @return 1 if bit is set, 0 otherwise
     */
    private int getBit(int input, int bit) {
        if ((input & (1 << bit)) > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    // begin extension by hao

    /**
     * Read a four byte big endian integer from file-stream
     */
    private int readBigEndianInt(FileInputStream in) throws IOException {
        return (in.read() << 24) | (in.read() << 16) | (in.read() << 8) | in.read();
    }
    // extension fix
}
