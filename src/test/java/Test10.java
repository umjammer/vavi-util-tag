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
import java.util.List;

import org.junit.jupiter.api.Disabled;

import vavix.util.screenscrape.annotation.InputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * Test10. Apple Artwork API
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/15 umjammer initial version <br>
 */
@Disabled
public class Test10 {

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

            String title = URLEncoder.encode(args[0], "UTF-8");
            String artist = URLEncoder.encode(args[1], "UTF-8");
//            String albumArtist = args[2];

            String urlString = String.format("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/coverArtMatch?an=%s&pn=%s", artist, title);
            URL url = new URL(urlString);

            HttpURLConnection uc = HttpURLConnection.class.cast(url.openConnection());
            uc.setRequestProperty("User-Agent", "iTunes/10.6.3 (Macintosh; Intel Mac OS X 10.7.4) AppleWebKit/534.56.5");
            uc.setRequestProperty("X-Apple-Store-Front", "143462-9,12");
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

            cache = new String(os.toByteArray());
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
        @Target("/plist/dict/key[text()='status']/following-sibling::string[1]/text()")
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
            StringBuilder sb = new StringBuilder();
            sb.append("status: ");
            sb.append(status);
            sb.append("\n");
            sb.append("coverArtUrl: ");
            sb.append(coverArtUrl);
            sb.append("\n");
            sb.append("requestDelaySeconds: ");
            sb.append(requestDelaySeconds);
            sb.append("\n");
            sb.append("artistName: ");
            sb.append(artistName);
            sb.append("\n");
            sb.append("playlistName: ");
            sb.append(playlistName);
            sb.append("\n");
            sb.append("artistId: ");
            sb.append(artistId);
            sb.append("\n");
            sb.append("playlistId: ");
            sb.append(playlistId);
            sb.append("\n");
            sb.append("matchType: ");
            sb.append(matchType);
            sb.append("\n");
            return sb.toString();
        }
    }

    /**
     *
     * @param argv
     */
    public static void main(String[] args) throws Exception {
        String title = "Rocks Off";
        String artist = "The Rolling Stones";
        List<Artwork> artworks = WebScraper.Util.scrape(Artwork.class, title, artist);
        System.out.println(artworks.get(0));
    }
}

/* */
