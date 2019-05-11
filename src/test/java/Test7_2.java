/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Disabled;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * Test7_2. (mp3 find unnecessary tags by directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
@Disabled
public class Test7_2 {

    static Logger logger = Logger.getLogger(Test7_2.class.getName());

    /**
     * @param args top_directory regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec7_2(args);
    }

    /** */
    private static void exec7_2(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
                    exec7_2(file.getAbsolutePath());
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
    private static void exec7_2(String mod) throws Exception {
        MP3File mp3File = new MP3File(mod);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
boolean first = true;
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (
                        key.equals("IEC ") ||

                        key.equals("UFID") ||
                        key.equals("NCON") ||
                        key.equals("GEOB") ||
                        key.equals("TSIZ") ||
                        key.equals("TLEN") ||
                        key.equals("TFLT") ||
                        key.equals("TMED") ||
                        key.equals("MCDI") ||
                        key.equals("TXXX") ||
                        key.equals("WXXX") ||
                        key.equals("PRIV")

                                           ||
                        key.equals("TPE3") ||
                        key.equals("TOWN") ||
                        key.equals("TGID") ||
                        key.equals("TDES") ||
                        key.equals("TCAT") ||
                        key.equals("TEXT") ||
                        key.equals("TPE4") ||
                        key.equals("TRSN") ||
                        key.equals("TOAL") ||
                        key.equals("TOPE") ||
                        key.equals("TOLY") ||
                        key.equals("TIT3") ||
                        key.equals("WOAS") ||
                        key.equals("WOAF") ||
                        key.equals("WFED") ||
                        key.equals("WORS")
                        ) {
first = printFrame(mod, key, first, frame);
                } else if (key.equals("TBPM")) {
                    Object o = frame.getContent("TBPM").getContent();
                    if (o instanceof String) {
                        if (!p_tpbm.matcher(String.class.cast(o)).matches()) {
first = printFrame(mod, key, first, frame);
                        }
                    }
                } else if (key.equals("TSRC")) {
                    Object o = frame.getContent("TSRC").getContent();
                    if (o instanceof String) {
                        if (!p_tsrc.matcher(String.class.cast(o)).matches()) {
first = printFrame(mod, key, first, frame);
                        }
                    }
                } else if (key.equals("TKEY")) {
                    Object o = frame.getContent("TKEY").getContent();
                    if (o instanceof String) {
                        if (!p_tkey.matcher(String.class.cast(o)).matches()) {
first = printFrame(mod, key, first, frame);
                        }
                    }
                } else if (key.equals("WOAR")) {
                    Object o = frame.getContent("WOAR").getContent();
                    if (o instanceof String) {
                        if (Collections.binarySearch(s_woar, String.class.cast(o)) < 0) {
first = printFrame(mod, key, first, frame);
                        }
                    }
                }
            }
        }
        if (mp3File.hasTag(Type.ID3v1)) {
System.err.println(mod + " has ID3v1");
        }
    }

    static final Pattern p_tpbm = Pattern.compile("\\d+");
    static final Pattern p_tsrc = Pattern.compile("[\\w-]+[\\d-]+");
    static final Pattern p_tkey = Pattern.compile("[ABCDEFGo][b# ]?m?");
    static final List<String> s_woar = new ArrayList<String>() {{
        add("www.nin.com");
    }};

    static boolean printFrame(String mod, String key, boolean first, ID3v2Frame frame) {
        if (first) {
            System.out.println(mod + " ------------");
            first = false;
        }
        System.out.println(key + ": " + frame.getContent(key) + "\n" + StringUtil.getDump(frame.getBytes(), 64));
        return first;
    }
}

/* */
