/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;
import vavi.util.tag.mp4.____;


/**
 * MP4ShowByWalkOneParDir. (mp4 by once a directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120608 nsano initial version <br>
 */
public class MP4ShowByWalkOneParDir {

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        exec8_1(args);
    }

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    private static void exec8_1(String[] args) throws Exception {
        AtomicReference<Path> dir = new AtomicReference<>();
        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    if (!file.getParent().equals(dir.get())) {
                        exec8_2(file.toAbsolutePath().toString());
//                    exec8_3(file.getAbsolutePath());
                        dir.set(file.getParent());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    /**
     * @param mod mp3
     */
    @SuppressWarnings("unchecked")
    private static void exec8_2(String mod) throws Exception {
        MP4File mp4File = new MP4File(mod);
        MP4Tag mp4Tag = (MP4Tag) mp4File.getTag();
        List<MP4Tag> results = (List<MP4Tag>) mp4Tag.getTag("----");
//System.err.println("results: " + results.size());
        for (Object o : results) {
            if (o instanceof ____ box) {
                if (box.getName().equals("iTunes_CDDB_1")) {
                    System.err.println(mod);
                    byte[] data = box.getData();
                    System.err.println(new String(data, 4, data.length - 4)); // TODO fixed value 4
                }
            }
        }
    }

//    /**
//     * print
//     * @param file
//     */
//    private static void exec8_3(String file) throws Exception {
//
//    }
}
