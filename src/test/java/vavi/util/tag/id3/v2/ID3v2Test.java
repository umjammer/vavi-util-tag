/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import org.junit.jupiter.api.Test;
import vavi.util.Debug;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavix.util.Checksum;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * ID3v2Test.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/03/20 umjammer initial version <br>
 */
public class ID3v2Test {

    String mp3 = "src/test/resources/test.mp3";

    String expected = "src/test/resources/comm_removed.mp3";

    @Test
    public void test() throws Exception {
        Path tmp = Path.of("tmp");
        if (!Files.exists(tmp)) {
            Files.createDirectories(tmp);
        }
        Path actual = tmp.resolve("target.mp3");
        Files.copy(Path.of(mp3), actual, StandardCopyOption.REPLACE_EXISTING);

        MP3File mp3File = new MP3File(actual.toString());

        ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
Debug.println(tag);
        Iterator<?> i = tag.tags();
        while (i.hasNext()) {
            ID3v2Frame frame = (ID3v2Frame) i.next();
            String key = frame.getID();
            if (key.equals("COMM")) {
                tag.removeFrame(frame);
Debug.println("remove COMM");
            }
        }
        tag.update();
Debug.println("update");

        assertEquals(Checksum.getChecksum(Path.of(expected)), Checksum.getChecksum(actual));
    }
}
