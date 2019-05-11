/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
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
 * Test7. (mp3 remove unnecessary tags by directory)
 * 
 * WORKS FINE
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
@Disabled
public class Test7 {

    static Logger logger = Logger.getLogger(Test7.class.getName());

    /**
     * @param args top_directory regex_pattern 
     */
    public static void main(String[] args) throws Exception {
        exec7_1(args);
    }

    /** */
    private static void exec7_1(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
                    exec7_2(file.getAbsolutePath());
                } catch (Exception e) {
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
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (
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
                        ) {
logger.info("remove " + key + ":\n" + StringUtil.getDump(frame.getBytes(), 64));
                    tag.removeFrame(frame);
                }
            }
            tag.update();
        }
    }
}

/* */
