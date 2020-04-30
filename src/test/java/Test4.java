/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.util.Iterator;

import org.junit.jupiter.api.Disabled;

import vavi.util.tag.Tag;
import vavi.util.tag.id3.ID3Tag;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavi.util.tag.id3.v2.impl.ID3v2FrameV230;

import vavix.util.FileUtil;


/**
 * Test4. (mp3 remove unnecessary tags test)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
@Disabled
public class Test4 {

    /**
     * @param args top_directory regex_pattern 
     */
    public static void main(String[] args) throws Exception {
        File orig = new File(args[0]);
        File mod = new File(args[1]);
        FileUtil.copy(orig, mod);
        Test4 app = new Test4();
        app.exec4_1(mod.getPath());
        app.exec4_2(orig.getPath());
        app.exec4_2(mod.getPath());
    }

    /**
     * @param mod a file those unnecessary tags will be removed
     */
    private void exec4_1(String mod) throws Exception {
        MP3File mp3File = new MP3File(mod);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (key.equals("LINK") ||
                    key.equals("PRIV")
                        ) {
System.out.println("remove " + key);
                    tag.removeFrame(frame);
                }
            }
            tag.update();
        }
    }

    /**
     * @param file 
     */
    private void exec4_2(String file) throws Exception {

        System.out.println("-------- " + file + " --------");

        MP3File mp3File = new MP3File(file);

        System.out.println("Bitrate:\t" + mp3File.getProperty("Bitrate"));
        System.out.println("Copyright:\t" + mp3File.getProperty("Copyright"));
        System.out.println("Layer:\t\t" + mp3File.getProperty("Layer"));
//      System.out.println("Duration:\t" + mp3File.getProperty("Duration"));
        System.out.println("MPEGLevel:\t" + mp3File.getProperty("MPEGLevel"));
        System.out.println("Mode:\t\t" + mp3File.getProperty("Mode"));
        System.out.println("Original:\t" + mp3File.getProperty("Original"));
        System.out.println("Padding:\t" + mp3File.getProperty("Padding"));
        System.out.println("Private:\t" + mp3File.getProperty("Private"));
        System.out.println("Protection:\t" + mp3File.getProperty("Protection"));
        System.out.println("Samplerate:\t" + mp3File.getProperty("SampleRate"));
        System.out.println("VBR:\t\t" + mp3File.getProperty("VBR"));

        System.out.println("Name:\t\t" + mp3File.getName());
        System.out.println("Path:\t\t" + mp3File.getPath());
        System.out.println("length:\t\t" + mp3File.length());
        System.out.printf("lastModified:\t%tc\n", mp3File.lastModified());

        for (ID3Tag.Type type : ID3Tag.Type.values()) {

            if (mp3File.hasTag(type)) {
                System.out.println("---- " + type + " ----");
                Tag tag = mp3File.getTag(type);

                switch (type) {
                case ID3v2:
                    System.out.println("Ver:\t" + ((ID3v2) tag).getVersion());
                    Iterator<?> i = tag.tags();
                    while (i.hasNext()) {
                        ID3v2Frame frame = (ID3v2Frame) i.next();
                        String key = frame.getID();
System.out.println(key + "=" + ((ID3v2FrameV230) frame).getContent());
                    }
                    System.out.println("UseCRC:\t" + ((ID3v2) tag).getUseCRC());
//                  System.out.println("UseCompression:\t" + ((ID3v2) tag).getUseCompression());
                    System.out.println("UsePadding:\t" + ((ID3v2) tag).getUsePadding());
                    System.out.println("UseUnsynchronization:\t" + ((ID3v2) tag).getUseUnsynchronization());
                    break;

                case ID3v1:
                    for (String key : new String[] { "Title", "Artist", "Album", "Year", "Genre", "Comment", "Track" }) {
                        String value = String.valueOf(tag.getTag(key));
System.out.println(key + "=" + value);
                    }
                    break;
                }
            }
        }
    }
}

/* */
