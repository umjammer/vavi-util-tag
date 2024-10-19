/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Logger;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;


/**
 * MP3RemoveTagUnnecessaryByWalk2. (mp3 remove unnecessary tags by directory)
 * <p>
 * WORKS FINE
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class MP3RemoveTagUnnecessaryByWalk2 {

    static Logger logger = Logger.getLogger(MP3RemoveTagUnnecessaryByWalk2.class.getName());

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec7_1(args);
    }

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    private static void exec7_1(String[] args) throws Exception {
        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    exec7_2(file.toAbsolutePath().toString());
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    /**
     * @param mod a file those unnecessary tags will be removed
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
