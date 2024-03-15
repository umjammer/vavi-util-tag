/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Properties;
import java.util.Scanner;

import vavi.util.tag.Tag;
import vavi.util.tag.id3.ID3Tag;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavi.util.tag.id3.v2.impl.ID3v2FrameV230;


/**
 * MP3ShowByList. (mp3 read from a file)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class MP3ShowByList {

    /**
     * @param args file_list_file
     */
    public static void main(String[] args) throws Exception {
        exec2(args);
    }

    /** */
    private static void exec2(String[] args) throws Exception {
        Scanner scanner = new Scanner(Files.newInputStream(Paths.get(args[0])));
        while (scanner.hasNextLine()) {
            try {
                String line = scanner.nextLine();
                exec2_1(line);
//break;
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        scanner.close();
    }

    /**
     * @param file
     */
    private static void exec2_1(String file) throws Exception {
        Properties props = new Properties();
        props.load(MP3File.class.getResourceAsStream("v2/impl/v230.properties"));

        MP3File mp3File = new MP3File(file);

        System.out.println("-------- " + file + " --------");

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

                if (type.equals(ID3Tag.Type.ID3v2)) {
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
                }

                if (type.equals(ID3Tag.Type.ID3v1)) {
                    for (String key : new String[] { "Title", "Artist", "Album", "Year", "Genre", "Comment", "Track" }) {
                        String value = String.valueOf(tag.getTag(key));
                        if (("Artist".equals(key) ||
                             "Title".equals(key) ||
                             "Album".equals(key)) && value.isEmpty() && !mp3File.hasTag(ID3Tag.Type.ID3v2)) {
System.err.println(key + "=" + "★★★ MISSING IMPORTANT ★★★: " + mp3File.getPath());
                        } else {
System.out.println(key + "=" + value);
                        }
                    }
                }
            }
        }
    }
}
