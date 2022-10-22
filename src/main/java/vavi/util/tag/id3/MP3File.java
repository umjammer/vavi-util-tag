/*
 * MP3.java
 * $Id: MP3File.java,v 1.2 2003/07/06 21:54:37 axelwernicke Exp $
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

package vavi.util.tag.id3;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import vavi.util.tag.Tag;
import vavi.util.tag.id3.v1.ID3v1;
import vavi.util.tag.id3.v1.ID3v1Exception;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * Instances of this class contain an MP3 file, giving access to its
 * ID3 and ID3v2 tags and other mp3 properties.
 * <p>
 * It provides a common interface to both tags, e.g. <code>setTitle(title)</code>
 * updates the title field in both tags. When reading (e.g. <code>getTitle()</code>,
 * it tries to provide as much information as possible (this means returning the
 * ID3v2 infos if tag and requested field are present).
 * <p>
 * Information stored in tags is always returned as a <code>TagContent</code>, the
 * description of the respective get Methods state which fields are used.
 * The more complex frames are not parsed into fields, but rather returned as a
 * byte array. It is up to the user of this class to make sense of it. Usage of
 * a special decode class is recommended.
 * <p>
 * It is assumed that each ID3v2 frame is unique, as is the case for nearly all
 * frame types
 *
 * @author Jens Vonderheide <jens@vdheide.de>
 */
public class MP3File extends File {

    /** */
    private static final Logger logger = Logger.getLogger(MP3File.class.getName());

    /** */
    private final Map<ID3Tag.Type, Tag> tags = new HashMap<>();

    /**
     * Creates a new instance.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP3File(String filename) throws IOException, ID3TagException {
        // initialize File
        super(filename);
        prop = new MP3Properties(this);
        readTags();
    }

    /** */
    private void readTags() throws IOException {
        // read properties and tags
        try {
            tags.put(ID3Tag.Type.ID3v1, new ID3v1(this));
        } catch (ID3v1Exception e) {
//logger.info("no ID3v1: " + this);
        }
        try {
            tags.put(ID3Tag.Type.ID3v2, new ID3v2(this));
        } catch (ID3v2Exception e) {
e.printStackTrace(System.err);
logger.info("no ID3v2: " + this);
        }
    }

    /**
     * Creates a MP3File instance that represents the file with the specified
     * name in the specified directory.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param dir Directory
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP3File(File dir, String filename) throws IOException, ID3TagException {
        super(dir, filename);
        prop = new MP3Properties(this);
        readTags();
    }

    /**
     * Creates a File instance whose pathname is the pathname of the specified directory,
     * followed by the separator character, followed by the name
     * argument.
     * Tag information is completely read the first time it is requested
     * and written after <code>update()</code>.
     *
     * @param dir Name of directory
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP3File(String dir, String filename) throws IOException, ID3TagException {
        super(dir, filename);
        prop = new MP3Properties(this);
        readTags();
    }

    /** */
    public Tag[] getTags() {
        return tags.values().toArray(new Tag[0]);
    }

    /** */
    public boolean hasTag(ID3Tag.Type type) {
        return tags.containsKey(type);
    }

    /** */
    public Tag getTag(ID3Tag.Type type) {
        return tags.get(type);
    }

    // Read MP3 properties

    /**
     * @param key
     * <li>"MPEG" level (1 or 2)
     * <li>"Layer" (1..3)
     * <li>"Bitrate"
     * <li>"SampleRate"
     * <li>"Mode" mode (mono, stereo, etc.) used in MP3 file
     *      Better use constants from MP3Properties.
     * <li>"Emphasis" Returns emphasis used in MP3 file
     *      Better use constants from MP3Properties.
     * <li>"Protection" (CRC) set
     * <li>"Private" bit set?
     * <li>"Padding" set?
     * <li>"Copyright" set?
     * <li>"Original"?
     * <li>"Length" in seconds
     * <li>"VBR" true, if the mp3 file is vbr
     */
    public Object getProperty(String key) {
        if ("MPEGLevel".equals(key)) {
            return prop.getMPEGLevel();
        } else if ("Layer".equals(key)) {
            return prop.getLayer();
        } else if ("Bitrate".equals(key)) {
            return prop.getBitrate();
        } else if ("SampleRate".equals(key)) {
            return prop.getSamplerate();
        } else if ("Mode".equals(key)) {
            return prop.getMode();
        } else if ("Emphasis".equals(key)) {
            return prop.getEmphasis();
        } else if ("Protection".equals(key)) {
            return prop.getProtection();
        } else if ("Private".equals(key)) {
            return prop.getPrivate();
        } else if ("Padding".equals(key)) {
            return prop.getPadding();
        } else if ("Copyright".equals(key)) {
            return prop.getCopyright();
        } else if ("Original".equals(key)) {
            return prop.getOriginal();
        } else if ("Length".equals(key)) {
            return prop.getLength();
        } else if ("VBR".equals(key)) {
            return prop.isVBR();
        } else {
            throw new IllegalArgumentException("unknown key: " + key);
        }
    }

    /**
     * MP3 properties
     */
    protected MP3Properties prop;
}

/* */
