/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;

import vavi.util.tag.Tag;
import vavi.util.tag.id3.ID3Tag;
import vavi.util.tag.id3.ID3TagException;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;


/**
 * Test11. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 100803 nsano initial version <br>
 */
public class Test11 {

    /**
     * @param args file 
     */
    public static void main(String[] args) throws Exception {
        exec1(args);
    }

    /** */
    private static void exec1(String[] args) throws Exception {
        exec1_1(new File(args[0]).getAbsolutePath());
    }

    /**
     * @param file 
     */
    private static void exec1_1(String file) throws Exception {
        Properties props = new Properties();
        props.load(MP3File.class.getResourceAsStream("v2/impl/v230.properties"));

        System.err.println("-------- " + file + " --------");

        MP3File mp3File = new MP3File(file);

        System.err.println("Bitrate:\t" + mp3File.getProperty("Bitrate"));
        System.err.println("Copyright:\t" + mp3File.getProperty("Copyright"));
        System.err.println("Layer:\t\t" + mp3File.getProperty("Layer"));
//      System.err.println("Duration:\t" + mp3File.getProperty("Duration"));
        System.err.println("MPEGLevel:\t" + mp3File.getProperty("MPEGLevel"));
        System.err.println("Mode:\t\t" + mp3File.getProperty("Mode"));
        System.err.println("Original:\t" + mp3File.getProperty("Original"));
        System.err.println("Padding:\t" + mp3File.getProperty("Padding"));
        System.err.println("Private:\t" + mp3File.getProperty("Private"));
        System.err.println("Protection:\t" + mp3File.getProperty("Protection"));
        System.err.println("Samplerate:\t" + mp3File.getProperty("SampleRate"));
        System.err.println("VBR:\t\t" + mp3File.getProperty("VBR"));

        System.err.println("Name:\t\t" + mp3File.getName());
        System.err.println("Path:\t\t" + mp3File.getPath());
        System.err.println("length:\t\t" + mp3File.length());
        System.err.printf("lastModified:\t%tc\n", mp3File.lastModified());

        for (ID3Tag.Type type : ID3Tag.Type.values()) {

            if (mp3File.hasTag(type)) {
                System.err.println("---- " + type + " ----");
                Tag tag = mp3File.getTag(type);

                if (type.equals(ID3Tag.Type.ID3v2)) {
                    System.err.println("Ver:\t" + ((ID3v2) tag).getVersion());
                    Enumeration<?> e = props.propertyNames();
                    while (e.hasMoreElements()) {
                        String key = (String) e.nextElement();
                        try {
                            String value = String.valueOf(tag.getTag(key));
                            if (("Artist".equals(key) ||
                                 "Title".equals(key) ||
                                 "Album".equals(key)) && value.length() == 0) {
System.err.println(key + "=" + "ÅöÅöÅö MISSING IMPORTANT ÅöÅöÅö: " + mp3File.getPath());
                            } else {
System.err.println(key + "=" + value);
                            }
                        } catch (ID3TagException f) {
//System.err.println(key + "=" + f.getMessage());
                        }
                    }
                    System.err.println("UseCRC:\t" + ((ID3v2) tag).getUseCRC());
//                  System.err.println("UseCompression:\t" + ((ID3v2) tag).getUseCompression());
                    System.err.println("UsePadding:\t" + ((ID3v2) tag).getUsePadding());
                    System.err.println("UseUnsynchronization:\t" + ((ID3v2) tag).getUseUnsynchronization());
                }

                if (type.equals(ID3Tag.Type.ID3v1)) {
                    for (String key : new String[] { "Title", "Artist", "Album", "Year", "Genre", "Comment", "Track" }) {
                        String value = String.valueOf(tag.getTag(key));
                        if (("Artist".equals(key) ||
                             "Title".equals(key) ||
                             "Album".equals(key)) && value.length() == 0 && !mp3File.hasTag(ID3Tag.Type.ID3v2)) {
System.err.println(key + "=" + "ÅöÅöÅö MISSING IMPORTANT ÅöÅöÅö: " + mp3File.getPath());
                        } else {
System.err.println(key + "=" + value);
                        }
                    }
                }
            }
        }
    }
}

/* */
