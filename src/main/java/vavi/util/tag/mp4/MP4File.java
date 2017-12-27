/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;
import vavi.util.tag.Tag;


/**
 * MP4File. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060403 nsano initial version <br>
 */
public class MP4File extends File {

    /** */
//  private static Logger logger = Logger.getLogger(MP4File.class.getName());

    private MP4Tag mp4Tag; 

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
        BoxFactory factory = BoxFactoryFactory.getFactory(MP4BoxFactory.class.getName());
        InputStream is = new FileInputStream(getPath());
        List<Box> boxes = new ArrayList<>();
        while (is.available() > 0) {
            Box box = factory.getInstance(is);
            boxes.add(box);
        }
        this.mp4Tag = new MP4Tag(boxes);
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

    /** */
    public Tag getTag() {
        return mp4Tag;
    }
}

/* */
