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
 * ITunesMusicLibrary. (itunes music library)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120614 nsano initial version <br>
 */
public class ITunesMusicLibrary {

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
            String sb = trackID +
                    "\t" +
                    artist +
                    "\t" +
                    name +
                    "\t" +
                    album +
                    "\t" +
                    persistentID +
                    "\t" +
                    location;
            return sb;
        }
        String itc(String pid) {

            String sb = "/Users/nsano/Music/iTunes/Album Artwork/Download/" +
                    pid +
                    '/' +
                    String.format("%02d", Integer.parseInt(persistentID.substring(15, 16), 16) & 0x0F) +
                    '/' +
                    String.format("%02d", Integer.parseInt(persistentID.substring(14, 15), 16) & 0x0F) +
                    '/' +
                    String.format("%02d", Integer.parseInt(persistentID.substring(13, 14), 16) & 0x0F) +
                    '/' + pid + "-" + persistentID +
                    ".itc";

            return sb;
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
