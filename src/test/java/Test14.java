/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import vavi.util.box.Box;
import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * Test14. (remove purchase and account data)
 *
 * <pre>
 * /moov/udta/meta/ilst/apID                                iTunes account used for purchase
 *                     /purd                                Purchase date
 * /moov/trak/mdia/minf/stbl/stsd/mp4a/pinf
 *                                         /frma
 *                                         /schm
 *                                         /schi
 *                                              /user
 *                                              /cert
 *                                              /righ
 *                                              /name       account name
 *                                              /chtb
 *                                         /sign
 * </pre>
 *
 * @see "https://gist.github.com/torque/4361328"
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120608 nsano initial version <br>
 */
public class Test14 {

    static class MyFileDigger implements FileDigger {
        private FileDredger dredger;

        public MyFileDigger(FileDredger dredger) {
            this.dredger = dredger;
        }

        public void dig(File file) throws IOException {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String fileName = line.split("\t")[0];

System.err.println(fileName);
                dredger.dredge(new File(fileName));
//break;
            }
            scanner.close();
        }
    }

    /**
     * @param args top_directory regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec14_1(args);
    }

    /** */
    private static void exec14_1(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
                        exec14_2(file.getAbsolutePath());
//                    exec8_3(file.getAbsolutePath());
                } catch (Exception e) {
                    System.err.println(file);
                    e.printStackTrace(System.err);
                }
            }
        }, Pattern.compile(args[1])).dig(new File(args[0]));
    }

    /**
     * @param mod mp4
     */
    private static void exec14_2(String mod) throws Exception {
        MP4File mp4File = new MP4File(mod);
        MP4Tag mp4Tag = (MP4Tag) mp4File.getTag();
        List<MP4Tag> results = (List) mp4Tag.getTag("name");
        for (Object o : results) {
            if (o instanceof Box) {
                Box box = (Box) o;
                byte[] d = box.getData();
                if (d[0] != 0 && d[1] != 0 && d[2] != 0 && d[3] != 0) {
                    String s = new String(d);
                    s = s.substring(0, s.indexOf(0));
                    if (!s.equals("直秀 佐野") && !s.equals("Naohide Sano")) {
                        System.err.println(mod + "\t" + s);
                    }
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
