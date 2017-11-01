/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.Iterator;

import vavi.util.StringUtil;
import vavi.util.tag.id3.ID3Tag;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;


/**
 * Test6. (mp3 remove unnecessary tags by one)
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051225 nsano initial version <br>
 */
public class Test6 {

    /**
     * @param args top_directory regex_pattern 
     */
    public static void main(String[] args) throws Exception {
        exec6_1(args[0]);
        exec6_2(args[0]);
    }

    /**
     * @param file 
     */
    private static void exec6_1(String mod) throws Exception {
        MP3File mp3File = new MP3File(mod);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (
//                    key.equals("UFID") ||
                    key.equals("NCON") ||
                    key.equals("TSIZ") ||
                    key.equals("TLEN") ||
                    key.equals("MCDI") ||
                    key.equals("TXXX") ||
                    key.equals("WXXX") ||
                    key.equals("PRIV")
                        ) {
System.err.println("remove " + key + ":\n" + StringUtil.getDump(frame.getBytes(), 64));
                    tag.removeFrame(frame);
                }
            }
            tag.update();
        }
    }

    /**
     * @param file 
     */
    private static void exec6_2(String file) throws Exception {

        System.out.println("---------------- " + file + " ----------------");

        MP3File mp3File = new MP3File(file);

//        System.out.println("Bitrate:\t" + mp3File.getProperty("Bitrate"));
//        System.out.println("Copyright:\t" + mp3File.getProperty("Copyright"));
//        System.out.println("Layer:\t\t" + mp3File.getProperty("Layer"));
//      System.out.println("Duration:\t" + mp3File.getProperty("Duration"));
//        System.out.println("MPEGLevel:\t" + mp3File.getProperty("MPEGLevel"));
//        System.out.println("Mode:\t\t" + mp3File.getProperty("Mode"));
//        System.out.println("Original:\t" + mp3File.getProperty("Original"));
//        System.out.println("Padding:\t" + mp3File.getProperty("Padding"));
//        System.out.println("Private:\t" + mp3File.getProperty("Private"));
//        System.out.println("Protection:\t" + mp3File.getProperty("Protection"));
//        System.out.println("Samplerate:\t" + mp3File.getProperty("SampleRate"));
//        System.out.println("VBR:\t\t" + mp3File.getProperty("VBR"));

//        System.out.println("Name:\t\t" + mp3File.getName());
//        System.out.println("Path:\t\t" + mp3File.getPath());
//        System.out.println("length:\t\t" + mp3File.length());
//        System.out.printf("lastModified:\t%s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(mp3File.lastModified())));

        for (ID3Tag.Type type : ID3Tag.Type.values()) {

            if (mp3File.hasTag(type)) {
//                Tag tag = mp3File.getTag(type);

                switch (type) {
                case ID3v2:
//                    System.out.println("---- " + type + " ----");
//                    System.out.println("Ver:\t" + ((ID3v2) tag).getVersion());
//                    Iterator<?> i = tag.tags();
//                    while (i.hasNext()) {
//                        ID3v2Frame frame = (ID3v2Frame) i.next();
//                        String key = frame.getID();
//System.out.println(key + "=" + ((ID3v2FrameV230) frame).getContent());
//                    }
//                    System.out.println("UseCRC:\t" + ((ID3v2) tag).getUseCRC());
//                  System.out.println("UseCompression:\t" + ((ID3v2) tag).getUseCompression());
//                    System.out.println("UsePadding:\t" + ((ID3v2) tag).getUsePadding());
//                    System.out.println("UseUnsynchronization:\t" + ((ID3v2) tag).getUseUnsynchronization());
                    break;

                case ID3v1:
                    System.out.println("★★★★★ " + type + " exists!!!");
                    break;
                }
            }
        }
    }
}

/* */
