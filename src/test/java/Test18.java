/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavix.util.screenscrape.annotation.JsonPathParser;
import vavix.util.screenscrape.annotation.PlainInputHandler;
import vavix.util.screenscrape.annotation.Target;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * iTunes Store Affiliate API
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/15 umjammer initial version <br>
 */
public class Test18 {

    /**
     * request parameters
     * <pre>
     * country     ISO Country code for iTunes Store
     * media       The media type you want to search for
     * [entity]    The type of results you want returned, relative to the specified media type
     * [attribute] The attribute you want to search for in the stores, relative to the specified media type
     * limit       Results limit
     * [offset]
     * [order]
     * lang        The language, English or Japanese, you want to use when returning search results
     * version     The search result key version you want to receive back from your search
     * explicit    A flag indicating whether or not you want to include explicit content in your search results
     * </pre>
     */
    @WebScraper(url = "https://itunes.apple.com/search?media=music&country={0}&term={1}",
                value = "$..results",
                isDebug = false,
                input = PlainInputHandler.class,
                parser = JsonPathParser.class)
    public static class Music {
        @Target String artistId;
        @Target String collectionId;
        @Target String trackId;
        @Target String artistName;
        @Target String collectionName;
        @Target String trackName;
        @Target String collectionCensoredName;
        @Target String trackCensoredName;
        @Target String collectionViewUrl;
        @Target String trackViewUrl;
        @Target String previewUrl;
        @Target String artworkUrl30;
        @Target String artworkUrl60;
        @Target String artworkUrl100;
        @Target String collectionPrice;
        @Target String trackPrice;
        @Target String trackHdPrice;
        @Target String trackCount;
        @Target String trackNumber;
        @Target String trackTimeMillis;
        @Target String country;
        @Target String currency;
        @Target String primaryGenreName;
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Artwork [artistId=")
                    .append(artistId)
                    .append(", collectionId=")
                    .append(collectionId)
                    .append(", trackId=")
                    .append(trackId)
                    .append(", artistName=")
                    .append(artistName)
                    .append(", collectionName=")
                    .append(collectionName)
                    .append(", trackName=")
                    .append(trackName)
                    .append(", collectionCensoredName=")
                    .append(collectionCensoredName)
                    .append(", trackCensoredName=")
                    .append(trackCensoredName)
                    .append(", collectionViewUrl=")
                    .append(collectionViewUrl)
                    .append(", trackViewUrl=")
                    .append(trackViewUrl)
                    .append(", previewUrl=")
                    .append(previewUrl)
                    .append(", artworkUrl30=")
                    .append(artworkUrl30)
                    .append(", artworkUrl60=")
                    .append(artworkUrl60)
                    .append(", artworkUrl100=")
                    .append(artworkUrl100)
                    .append(", collectionPrice=")
                    .append(collectionPrice)
                    .append(", trackPrice=")
                    .append(trackPrice)
                    .append(", trackHdPrice=")
                    .append(trackHdPrice)
                    .append(", trackCount=")
                    .append(trackCount)
                    .append(", trackNumber=")
                    .append(trackNumber)
                    .append(", trackTimeMillis=")
                    .append(trackTimeMillis)
                    .append(", country=")
                    .append(country)
                    .append(", currency=")
                    .append(currency)
                    .append(", primaryGenreName=")
                    .append(primaryGenreName)
                    .append("]");
            return builder.toString();
        }
    }

    /**
     *
     * @param args 0: country, 1: term
     */
    public static void main(String[] args) throws Exception {
        Test18 app = new Test18();
        app.exec(args);
    }

    private BufferedImage image;

    /**
     * @param args 0: country, 1: term
     */
    void exec(String[] args) throws IOException {
        String country = "jp"; //args[0];
        String term = "Aimer"; //args[1];
        List<Music> musics = WebScraper.Util.scrape(Music.class, country, term);

        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        musics.forEach(m -> {
//System.err.println(m);
            System.out.printf("%s - %s: %s\n", m.artistName, m.collectionName, m.artworkUrl100);
            try {
                image = ImageIO.read(URI.create(m.artworkUrl100.replace("100x100", "600x600")).toURL());
                frame.setTitle(m.artworkUrl100);
                panel.repaint();
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }
}

/* */
