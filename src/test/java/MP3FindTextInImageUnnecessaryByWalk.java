/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;


/**
 * MP3FindTextInImageUnnecessaryByWalk. (mp3 find unnecessary text in image by directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class MP3FindTextInImageUnnecessaryByWalk {

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec16(args);
    }

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    private static void exec16(String[] args) throws Exception {
        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    try {
                        exec16(file.toAbsolutePath().toString());
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
     * @param file mp3
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
