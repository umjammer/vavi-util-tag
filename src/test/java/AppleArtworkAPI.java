/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * AppleArtworkAPI. Apple Artwork API
 *
 * TODO deprecated ???
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/15 umjammer initial version <br>
 */
public class AppleArtworkAPI {

    /** */
    public static class MyInput implements InputHandler<Reader> {
        String cache;
        /**
         * @param args 0: title, 1: artist, 2: albumArtist
         */
        public Reader getInput(String... args) throws IOException {
            if (cache != null) {
                return new StringReader(cache);
            }

            String title = URLEncoder.encode(args[0], StandardCharsets.UTF_8);
            String artist = URLEncoder.encode(args[1], StandardCharsets.UTF_8);
//            String albumArtist = args[2];
System.err.println("title: " + args[0]);
System.err.println("artist: " + args[1]);

            // ann={albumArtist}
            String urlString = String.format("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/coverArtMatch?ann=%s&pn=%s", artist, title);
            URL url = new URL(urlString);

            HttpURLConnection uc = (HttpURLConnection) url.openConnection();
            uc.setRequestProperty("User-Agent", "iTunes/10.6.3 (Macintosh; Intel Mac OS X 10.7.4) AppleWebKit/534.56.5");
            uc.setRequestProperty("X-Apple-Store-Front", "143441-1"); // 143462-9,12
            uc.connect();
System.err.println("url: " + url);
System.err.println("result: " + uc.getResponseCode());

            InputStream is = uc.getInputStream();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            while (true) {
                int r = is.read(buf);
                if (r < 0) {
                    break;
                }
                os.write(buf, 0, r);
            }

            cache = os.toString();
System.err.println(cache);
//try {
// InputSource in = new InputSource(new AsciizBinder(cache));
// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
// DocumentBuilder db = dbf.newDocumentBuilder();
// new PrettyPrinter(System.err).print(db.parse(in));
//} catch (Exception e) {
// e.printStackTrace(System.err);
//}

            return new StringReader(cache);
        }
    }

    /** */
    @WebScraper(input = MyInput.class,
                isCollection = false)
    public static class Artwork {
        @Target("/plist/dict/key[text()='status']/following-sibling::integer[1]/text()")
        String status;
        @Target("/plist/dict/key[text()='cover-art-url']/following-sibling::string[1]/text()")
        String coverArtUrl;
        @Target("/plist/dict/key[text()='request-delay-seconds']/following-sibling::string[1]/text()")
        String requestDelaySeconds;
        @Target("/plist/dict/key[text()='artistName']/following-sibling::string[1]/text()")
        String artistName;
        @Target("/plist/dict/key[text()='playlistName']/following-sibling::string[1]/text()")
        String playlistName;
        @Target("/plist/dict/key[text()='artistId']/following-sibling::string[1]/text()")
        String artistId;
        @Target("/plist/dict/key[text()='playlistId']/following-sibling::string[1]/text()")
        String playlistId;
        @Target("/plist/dict/key[text()='matchType']/following-sibling::string[1]/text()")
        String matchType;
        public String toString() {
            String sb = "status: " +
                    status +
                    "\n" +
                    "coverArtUrl: " +
                    coverArtUrl +
                    "\n" +
                    "requestDelaySeconds: " +
                    requestDelaySeconds +
                    "\n" +
                    "artistName: " +
                    artistName +
                    "\n" +
                    "playlistName: " +
                    playlistName +
                    "\n" +
                    "artistId: " +
                    artistId +
                    "\n" +
                    "playlistId: " +
                    playlistId +
                    "\n" +
                    "matchType: " +
                    matchType +
                    "\n";
            return sb;
        }
    }

    /**
     *
     * @param args 0: title, 1: artist
     */
    public static void main(String[] args) throws Exception {
        String title = "Kansas"; //args[0];
        String artist = "Kansas"; //args[1];
        Artwork artwork = WebScraper.Util.scrape(Artwork.class, title, artist).get(0);
        if (artwork.status.equals("3004")) {
            System.err.println("not found for " + artist + " - " + title);
        } else {
            System.out.println(artwork);
        }
    }
}
