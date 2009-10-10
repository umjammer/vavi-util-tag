/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.File;
import java.io.IOException;


/**
 * MP4File. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060403 nsano initial version <br>
 */
public class MP4File extends File {

    /** */
//  private static Logger logger = Logger.getLogger(MP4File.class.getName());

    /**
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP4File(String filename) throws IOException {
        // initialize File
        super(filename);
        readTags();
    }

    /** */
    private void readTags() throws IOException {
    }

    /**
     * @param dir Directory
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP4File(File dir, String filename) throws IOException {
        super(dir, filename);
        readTags();
    }

    /**
     * @param dir Name of directory
     * @param filename File name
     * @throws IOException If I/O error occurs
     */
    public MP4File(String dir, String filename) throws IOException {
        super(dir, filename);
        readTags();
    }

    //----

    /** */
    public static void main(String[] args) {
        
    }
}

/* */
