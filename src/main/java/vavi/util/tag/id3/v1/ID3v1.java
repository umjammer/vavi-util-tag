// ID3.java
//
// $Id: ID3.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
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

package vavi.util.tag.id3.v1;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import vavi.util.tag.TagException;
import vavi.util.tag.id3.CharConverter;
import vavi.util.tag.id3.GenreUtil;
import vavi.util.tag.id3.ID3Tag;


/**
 * Class to read and modify ID3 tags on MP3 files.
 * <p>
 * ID3 information are loaded
 * <li> the first time any of these is requested
 * <li> after doing a readTag()
 * <li> after changing any of these (no real reload, it is just changed)
 * </p>
 * <p>
 * ID3 information are written
 * <li> after doing a writeTag()
 * </p>
 * <p>
 * If a file does not contain an ID3 tag, each read access will throw a
 * NoID3TagException. A write access will create an ID3 tag if none is present.
 * </p>
 */
public class ID3v1 implements ID3Tag, Serializable {
    /** jdk1.4 logger */
    private static final Logger logger = Logger.getLogger(ID3v1.class.getName());

    /**
     * encoding to use when converting from Unicode (String) to bytes
     * IETF says the encoding should be ISO-8859-1
     */
    private static String encoding;

    /** */
    static {
        try {
            Properties props = new Properties();
            props.load(ID3v1.class.getResourceAsStream("/vavi/util/tag/id3/id3.properties"));
            encoding = props.getProperty("id3.encoding");
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    /**
     * Create a new ID3 tag which is based on mp3_file
     *
     * @param file MP3 file to read ID3 tag to / write ID3 tag to
     * @throws ID3v1MissingTagException
     */
    public ID3v1(File file) throws ID3v1Exception, IOException {
        this.file = file;
        readTag();
    }

    /**
     * @param key
     * <br>"Title"
     * <br>"Artist"
     * <br>"Album"
     * <br>"Year" Integer
     * <br>"Genre" String @see GenreUtil
     * <br>"Comment"
     * <br>"Track" Integer
     * @throws IllegalArgumentException unknown key
     */
    public Object getTag(String key) {
        if ("Title".equals(key)) {
            return getTitle();
        } else if ("Artist".equals(key)) {
            return getArtist();
        } else if ("Album".equals(key)) {
            return getAlbum();
        } else if ("Year".equals(key)) {
            return getYear();
        } else if ("Genre".equals(key)) {
            return GenreUtil.getGenreString(getGenre());
        } else if ("Comment".equals(key)) {
            return getComment();
        } else if ("Track".equals(key)) {
            return getTrack();
        } else {
            throw new IllegalArgumentException("unknown key: " + key);
        }
    }

    /**
     * @param key
     * @throws IllegalArgumentException unknown key
     */
    public void setTag(String key, Object value) {
        if ("Title".equals(key)) {
            setTitle((String) value);
        } else if ("Artist".equals(key)) {
            setArtist((String) value);
        } else if ("Album".equals(key)) {
            setAlbum((String) value);
        } else if ("Year".equals(key)) {
            setYear((Integer) value);
        } else if ("Genre".equals(key)) {
            setGenre((Integer) value);
        } else if ("Comment".equals(key)) {
            setComment((String) value);
        } else if ("Track".equals(key)) {
            setTrack((Integer) value);
        } else {
            throw new IllegalArgumentException("unknown key: " + key);
        }
    }

    /**
     * Read title from ID3 tag
     *
     * @returns Title
     */
    private String getTitle() {
        return title;
    }

    /**
     * Read artist from ID3 tag
     *
     * @returns Artist
     */
    private String getArtist() {
        return artist;
    }

    /**
     * Read album from ID3 tag
     *
     * @returns album
     */
    private String getAlbum() {
        return album;
    }

    /**
     * Read year from ID3 tag
     *
     * @returns Year
     */
    private int getYear() {
        return year;
    }

    /**
     * Read genre from ID3 tag
     *
     * @returns Genre
     */
    private int getGenre() {
        return genre;
    }

    /**
     * Read comment from ID3 tag
     *
     * @returns comment
     */
    private String getComment() {
        return comment;
    }

    /**
     * Read track number from ID3 tag
     *
     * @returns Track number
     */
    private int getTrack() {
        return track;
    }

    /**
     * Read ID3 tag and prepare for retrieval with getXXX
     * Use this method to reread tag if changed externally
     *
     * @throws ID3v1MissingTagException If file does not contain an ID3 tag
     * @throws IOException If I/O error occurs
     */
    private void readTag() throws ID3v1Exception, IOException {
        // get access to file
        RandomAccessFile in = new RandomAccessFile(file, "r");

        // file is now prepared
        // check for ID3 tag
        if (!checkForTag()) {
            // axel.wernicke@gmx.de fix begin
            // we should close the file when leaving the method that opened it ...
            in.close();
            in = null;
            // axel.wernicke@gmx.de fix end
            // No ID3 tag found
            throw new ID3v1Exception("missing tag");
        } else {
            // ID3 tag found, read it
            in.seek(in.length() - 125);
            byte[] buffer = new byte[125];
            if (in.read(buffer, 0, 125) != 125) {
                logger.warning("tag too short");
                // this cannot happen because we found "TAG" at correct position
            }
//          String tag = new String(buffer, 0, 125, encoding);

            // cut tag;
            title = CharConverter.createString2(buffer, 0, 30).trim();
            artist = CharConverter.createString2(buffer, 30, 30).trim();
            album = CharConverter.createString2(buffer, 60, 30).trim();
            try {
                year = Integer.parseInt(new String(buffer, 90, 4).trim());
            } catch (NumberFormatException e) {
//                logger.warning(e.toString());
                year = 0;
            }
            comment = CharConverter.createString2(buffer, 94, 29).trim();
            track = buffer[123] & 0xff;
            genre = buffer[124] & 0xff;
        }

        in.close();
        // axel.wernicke@gmx.de fix begin
        in = null;
        // axel.wernicke@gmx.de fix end
    }

    /**
     * Set title
     *
     * @param title Title
     */
    private void setTitle(String title) {
        this.title = title;
    }

    /**
     * Set artist
     *
     * @param artist Artist
     */
    private void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Set album
     *
     * @param album Album
     */
    private void setAlbum(String album) {
        this.album = album;
    }

    /**
     * Set year
     *
     * @param year Year
     */
    private void setYear(int year) {
        this.year = year;
    }

    /**
     * Set comment
     *
     * @param comment Comment
     */
    private void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Set track number
     *
     * @param track Track number
     * @throws IllegalArgumentException if track is negative or
     *         larger than 255
     */
    private void setTrack(int track) {
        if (track < 0 || track > 255) {
            throw new IllegalArgumentException(String.valueOf(track));
        } else {
            this.track = track;
        }
    }

    /**
     * Set genre
     *
     * @param genre Genre
     * @throws IllegalArgumentException if genre is negative or
     *         larger than 255
     */
    private void setGenre(int genre) {
        if (track < 0 || track > 255) {
            throw new IllegalArgumentException(String.valueOf(genre));
        } else {
            this.genre = genre;
        }
    }

    /**
     * Write information provided with setXXX to ID3 tag
     */
    public void update() throws IOException {
        // get access to file
        RandomAccessFile in = new RandomAccessFile(file, "rw");

        // file is now prepared
        // check for ID3 tag
        if (!checkForTag()) {
            // No ID3 tag found, create new
            // seek to end of file
            in.seek(in.length());
        } else {
            // jump to "TAG"
            in.seek(in.length() - 128);
        }

        // write new tag
        in.write("TAG".getBytes());
        in.write(fillWithNills(title, 30).getBytes(encoding));
        in.write(fillWithNills(artist, 30).getBytes(encoding));
        in.write(fillWithNills(album, 30).getBytes(encoding));
        in.write(fillWithNills(String.valueOf(year), 4).getBytes());
        in.write(fillWithNills(comment, 29).getBytes(encoding));
        in.writeByte(track);
        in.writeByte(genre);

        in.close();
    }

    /** file to access */
    private final File file;
    /** id3 title */
    private String title;
    /** id3 artist */
    private String artist;
    /** id3 album */
    private String album;
    /** id3 year */
    private int year;
    /** id3 genre, -1 == not set */
    private int genre;
    /** id3 comment */
    private String comment;
    /** id3 track number */
    private int track;

    /**
     * Check if ID3 tag is present
     *
     * @returns true if tag present
     */
    private boolean checkForTag() throws IOException {
        // Create random access file
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        if (raf.length() < 129) {
            // begin fix by axel.wernicke@gmx.de 02/12/26
logger.warning("mp3 length < 129: " + raf.length());
            raf.close();
            raf = null;
            // end fix
            // file to short for an ID3 tag
            return false;
        } else {
            // go to position where "TAG" must be
            long seekPos = raf.length() - 128;
            raf.seek(seekPos);

            byte[] buffer = new byte[3];

            if (raf.read(buffer, 0, 3) != 3) {
                // begin fix by axel.wernicke@gmx.de 02/12/26
                raf.close();
                raf = null;
                // end fix
                // something terrible happened
                throw new EOFException("Read beyond end of file");
            }

            // begin fix by axel.wernicke@gmx.de 02/12/26
            raf.close();
            raf = null;
            // end fix
            String testTag = new String(buffer, 0, 3);
            if (!testTag.equals("TAG")) {
//logger.warning("unknown type: " + testTag);
                return false;
            } else {
                return true;
            }
        }
    }

    /**
     * Fill <tt>str</tt> with \0 until <tt>str</tt> has length <tt>len</tt>
     *
     * @param str String to work with
     * @param len Length of <tt>str</tt> after filling
     * @return Filled string
     */
    private String fillWithNills(String str, int len) throws IOException {
        if (str == null) {
            // tag info not set!
            str = "";
        }
        StringBuilder tmp = new StringBuilder(str);
        tmp.append("\0".repeat(Math.max(0, len - (str.getBytes(encoding).length + 1) + 1)));
        return tmp.toString();
    }

    /* */
    public Iterator<?> tags() throws TagException {
        List<Object> results = new ArrayList<>();
        results.add(getTitle());
        results.add(getArtist());
        results.add(getAlbum());
        results.add(getYear());
        results.add(GenreUtil.getGenreString(getGenre()));
        results.add(getComment());
        results.add(getTrack());
        return results.iterator();
    }
}
