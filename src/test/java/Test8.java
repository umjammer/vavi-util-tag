/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.Ignore;

import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;
import vavi.util.tag.mp4.____;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * Test8. (mp4 by once a directory)
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 120608 nsano initial version <br>
 */
@Ignore
public class Test8 {

    /**
     * @param args top_directory regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec8_1(args);
    }

    /** */
    private static void exec8_1(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            String dir;
            public void dredge(File file) throws IOException {
                try {
                    if (!file.getParent().equals(dir)) {
                        exec8_2(file.getAbsolutePath());
//                    exec8_3(file.getAbsolutePath());
                        dir = file.getParent();
                    }
                } catch (Exception e) {
                    System.err.println(file);
                    e.printStackTrace(System.err);
                }
            }
        }, Pattern.compile(args[1])).dig(new File(args[0]));
    }

    /**
     * @param mod
     */
    private static void exec8_2(String mod) throws Exception {
        MP4File mp4File = new MP4File(mod);
        MP4Tag mp4Tag = MP4Tag.class.cast(mp4File.getTag());
        List<MP4Tag> results = List.class.cast(mp4Tag.getTag("----"));
//System.err.println("results: " + results.size());
        for (Object o : results) {
            if (____.class.isInstance(o)) {
                ____ box = ____.class.cast(o);
                if (box.getName().equals("iTunes_CDDB_1")) {
                    System.err.println(mod);
                    byte[] data = box.getData();
                    System.err.println(new String(data, 4, data.length - 4)); // TODO fixed value 4
                }
            }
        }
    }

    /**
     * print
     * @param file
     */
//    private static void exec8_3(String file) throws Exception {
//
//    }
}

/* */
