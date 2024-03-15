/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
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


/**
 * MP3ShowTagUnnecessaryByWalk. (mp3 find unnecessary tags by directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
@Disabled
public class MP3ShowTagUnnecessaryByWalk {

    static Logger logger = Logger.getLogger(MP3ShowTagUnnecessaryByWalk.class.getName());

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec7_2(args);
    }

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    private static void exec7_2(String[] args) throws Exception {
        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    try {
                        exec7_2(file.toAbsolutePath().toString());
                    } catch (FileNotFoundException e) { // for mac jvm6 bug?
                        System.err.println(file + " ------------");
                        System.err.println("exists?: " + Files.exists(file));
                        Path newFile = Files.createFile(file);
                        System.err.println("exists?: " + Files.exists(newFile));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    /**
     * @param mod a file those unnecessary tags will be shown
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
                switch (key) {
                case "IEC ", "UFID", "NCON", "GEOB", "TSIZ", "TLEN", "TFLT", "TMED", "MCDI", "TXXX", "WXXX", "PRIV", "TPE3", "TOWN", "TGID", "TDES", "TCAT", "TEXT", "TPE4", "TRSN", "TOAL", "TOPE", "TOLY", "TIT3", "WOAS", "WOAF", "WFED", "WORS" ->
                        first = printFrame(mod, key, first, frame);
                case "TBPM" -> {
                    Object o = frame.getContent("TBPM").getContent();
                    if (o instanceof String) {
                        if (!p_tpbm.matcher((String) o).matches()) {
                            first = printFrame(mod, key, first, frame);
                        }
                    }
                }
                case "TSRC" -> {
                    Object o = frame.getContent("TSRC").getContent();
                    if (o instanceof String) {
                        if (!p_tsrc.matcher((String) o).matches()) {
                            first = printFrame(mod, key, first, frame);
                        }
                    }
                }
                case "TKEY" -> {
                    Object o = frame.getContent("TKEY").getContent();
                    if (o instanceof String) {
                        if (!p_tkey.matcher((String) o).matches()) {
                            first = printFrame(mod, key, first, frame);
                        }
                    }
                }
                case "WOAR" -> {
                    Object o = frame.getContent("WOAR").getContent();
                    if (o instanceof String) {
                        if (Collections.binarySearch(s_woar, (String) o) < 0) {
                            first = printFrame(mod, key, first, frame);
                        }
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
    static final List<String> s_woar = new ArrayList<>() {{
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
