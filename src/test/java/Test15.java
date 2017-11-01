/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Iterator;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;


/**
 * Test15. (mp3 remove specified tags by one)
 * 
 * WORKS FINE
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class Test15 {

    /**
     * @param args top_directory regex_pattern 
     */
    public static void main(String[] args) throws Exception {
        exec15_1(args);
    }

    /**
     * @param file 
     */
    private static void exec15_1(String[] args) throws Exception {
System.err.println("file: " + args[0]);
        MP3File mp3File = new MP3File(args[0]);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                for (int j = 1; j < args.length; j++) {
                    if (key.equals(args[j])) {
System.err.println("remove " + key + ":\n" + StringUtil.getDump(frame.getBytes(), 64));
                        tag.removeFrame(frame);
                    }
                }
            }
            tag.update();
        }
    }
}

/* */
