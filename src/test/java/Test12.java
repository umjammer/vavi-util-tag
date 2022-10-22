/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.List;

import vavix.util.screenscrape.annotation.SaxonXPathParser;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Test12. (itunes music library)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120614 nsano initial version <br>
 */
public class Test12 {

    /** iTunes ライブラリ一 */
    @WebScraper(url = "file:///Users/nsano/Music/iTunes/iTunes%20Music%20Library.xml",
                isCollection = false)
    public static class Meta {
        @Target("/plist/dict/key[text()='Library Persistent ID']/following-sibling::string[1]/text()")
        String libraryPersistentID;
        public String toString() {
            return libraryPersistentID;
        }
    }

    /** iTunes ライブラリ一曲 */
    @WebScraper(url = "file:///Users/nsano/Music/iTunes/iTunes%20Music%20Library.xml",
                parser = SaxonXPathParser.class,
                value = "/plist/dict/dict/dict")
    public static class Song {
        @Target("/dict/key[text()='Track ID']/following-sibling::string[1]/text()")
        String trackID;
        @Target("/dict/key[text()='Artist']/following-sibling::string[1]/text()")
        String artist;
        @Target("/dict/key[text()='Name']/following-sibling::string[1]/text()")
        String name;
        @Target("/dict/key[text()='Album']/following-sibling::string[1]/text()")
        String album;
        @Target("/dict/key[text()='Persistent ID']/following-sibling::string[1]/text()")
        String persistentID;
        @Target("/dict/key[text()='Location']/following-sibling::string[1]/text()")
        String location;
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(trackID);
            sb.append("\t");
            sb.append(artist);
            sb.append("\t");
            sb.append(name);
            sb.append("\t");
            sb.append(album);
            sb.append("\t");
            sb.append(persistentID);
            sb.append("\t");
            sb.append(location);
            return sb.toString();
        }
        String itc(String pid) {
            StringBuilder sb = new StringBuilder();
            sb.append("/Users/nsano/Music/iTunes/Album Artwork/Download/");
            sb.append(pid);
            sb.append('/');
            sb.append(String.format("%02d", Integer.parseInt(persistentID.substring(15, 16), 16) & 0x0F));
            sb.append('/');
            sb.append(String.format("%02d", Integer.parseInt(persistentID.substring(14, 15), 16) & 0x0F));
            sb.append('/');
            sb.append(String.format("%02d", Integer.parseInt(persistentID.substring(13, 14), 16) & 0x0F));

            sb.append('/').append(pid).append("-").append(persistentID);

            sb.append(".itc");

            return sb.toString();
        }
    }

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        List<Meta> metas = WebScraper.Util.scrape(Meta.class);
        String libraryPersistentID = metas.get(0).libraryPersistentID;
System.err.println("Library Persistent ID: " + libraryPersistentID);
//        final String libraryPersistentID = "0C01A45070E6DBEE";
        WebScraper.Util.foreach(Song.class, each -> {
            String itc = each.itc(libraryPersistentID);
//            if (new File(itc).exists()) {
                System.err.println("songs: " + itc);
//            }
        });
    }
}

/* */
