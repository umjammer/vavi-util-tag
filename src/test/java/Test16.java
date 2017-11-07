/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * Test16. (mp3 find unnecessary text in image by directory)
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class Test16 {

    static Logger logger = Logger.getLogger(Test16.class.getName());

    /**
     * @param args top_directory regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec16(args);
    }

    /** */
    private static void exec16(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
                    exec16(file.getAbsolutePath());
} catch (FileNotFoundException e) { // for mac jvm6 bug?
 System.err.println(file + " ------------");
 System.err.println("exists?: " + file.exists());
 File newFile = new File(file.getParentFile(), file.getName());
 System.err.println("exists?: " + newFile.exists());
                } catch (Exception e) {
                    System.err.println(file + " ------------");
                    e.printStackTrace();
                }
            }
        }, Pattern.compile(args[1])).dig(new File(args[0]));
    }

    /**
     * @param file
     */
    private static void exec16(String file) throws Exception {
        if (file.toLowerCase().endsWith(".mp3")) {
            exec16_mp3(file);
        } else {
            exec16_m4a(file);
        }
    }

    private static void exec16_mp3(String mod) throws Exception {
        MP3File mp3File = new MP3File(mod);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
boolean first = true;
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (key.equals("APIC")) {
first = printFrame(mod, key, first, frame);
                }
            }
        }
    }

    private static void exec16_m4a(String mod) throws Exception {

    }

    static boolean printFrame(String mod, String key, boolean first, ID3v2Frame frame) {
        if (first) {
            System.out.println(mod + " ------------");
            first = false;
        }
        System.out.println(key + ": " + frame.getContent(key) + "\n" + StringUtil.getDump(frame.getBytes(), 128));
        return first;
    }
}

/* */
