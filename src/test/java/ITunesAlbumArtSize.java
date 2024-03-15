/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.kafsemo.titl.Library;
import org.kafsemo.titl.ParseLibrary;
import org.kafsemo.titl.Track;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;
import vavi.util.itunes.artwork.ITCBoxFactory;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;
import vavix.util.screenscrape.annotation.WebScraper;


/**
 * ITunesAlbumArtSize. (album art size by directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120613 nsano initial version <br>
 */
public class ITunesAlbumArtSize {

    static class ApidDao {
        Connection connection;
        ApidDao() {
            try {
                connection = DriverManager.getConnection("jdbc:hsqldb:file:tmp/apiddb", "SA", "");

                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE apid IF EXISTS;");
                statement.execute("CREATE TABLE apid(name VARCHAR, artist VARCHAR, apid VARCHAR);");
                statement.close();

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO apid VALUES (?, ?, ?);");

                Library lib = ParseLibrary.parse(new File("/Users/nsano/Music/iTunes/iTunes Library.itl"));
                for (Track t : lib.getTracks()) {
                    preparedStatement.setString(1, t.getName());
                    preparedStatement.setString(2, t.getArtist());
                    String apid = new String(t.getAlbumPersistentId(), StandardCharsets.US_ASCII);
                    if (apid.isEmpty()) {
                        apid = new String(t.getPersistentId(), StandardCharsets.US_ASCII);
                        if (apid.isEmpty()) {
                            System.err.println("apid not found: " + t.getName() + ", " + t.getArtist());
                        }
                    }
                    preparedStatement.setString(3, apid);
                    preparedStatement.execute();
                }

                preparedStatement.close();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        String getApid(String name, String artist) {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT apid FROM apid WHERE name = ? AND artist = ?;");
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, artist);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString(1);
                }
                return null;
            } catch (SQLException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    static ApidDao dao;

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        dao = new ApidDao();

        exec9_1(args);
    }

    /** */
    private static void exec9_1(String[] args) throws Exception {
        AtomicReference<Path> dir = new AtomicReference<>();
        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    if (!file.getParent().equals(dir.get())) {
                        boolean found = exec9_2(file.toFile());
                        Thread.sleep(1000);
                        if (found) {
                            dir.set(file.getParent());
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    static AppleArtworkAPI.Artwork queue;

    /** */
    private static boolean exec9_2(File file) throws Exception {
        boolean found;
        queue = null;
        if (file.getName().toLowerCase().endsWith(".mp3")) {
            found = exec9_3(file);
        } else {
            found = exec9_4(file);
        }
        if (queue != null) {
            System.err.println(queue);
            System.out.print(file.getParent() + "\t");
            System.out.print(file.getName().substring(file.getName().lastIndexOf('.') + 1).toLowerCase() + "\t");
            System.out.print(queue.coverArtUrl);
            System.out.println();
        }
        return found || queue != null;
    }

    /**
     * @param file m4a
     */
    @SuppressWarnings("unchecked")
    private static boolean exec9_4(File file) throws Exception {

        boolean found = false;

        MP4File mp4File = new MP4File(file.getAbsolutePath());

        MP4Tag tag = (MP4Tag) mp4File.getTag();
        String name = new String(((List<Box>) tag.getTag((char) 0xa9 + "nam")).get(0).getData()).substring(8);
        String artist = new String(((List<Box>) tag.getTag((char) 0xa9 + "ART")).get(0).getData()).substring(8);
        found = itcImage(file, name, artist);
        if (!found) {
            List<AppleArtworkAPI.Artwork> artworks = WebScraper.Util.scrape(AppleArtworkAPI.Artwork.class, name, artist);
            queue = artworks.get(0);
        }

        return found;
    }

    static BoxFactory itcFactory = BoxFactoryFactory.getFactory(ITCBoxFactory.class.getName());

    /**
     * @param file mp3
     */
    private static boolean exec9_3(File file) throws Exception {

        boolean found = false;

        MP3File mp3File = new MP3File(file.getAbsolutePath());

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            String name = (String) tag.getTag("Title");
            String artist = (String) tag.getTag("Artist");
            found = itcImage(file, name, artist);
            if (!found) {
                List<AppleArtworkAPI.Artwork> artworks = WebScraper.Util.scrape(AppleArtworkAPI.Artwork.class, name, artist);
                queue = artworks.get(0);
            }
        } else {
            Debug.println("no id3v2 tag");
        }

        return found;
    }

    static boolean itcImage(File file, String name, String artist) throws IOException {
        boolean found = false;
        if (name.charAt(name.length() - 1) == 0) {
            name = name.substring(0, name.length() - 1);
        }
        if (artist.charAt(artist.length() - 1) == 0) {
            artist = artist.substring(0, artist.length() - 1);
        }
        String apid = dao.getApid(name, artist);
        if (apid != null) {
            File itc = new File(itc("0C01A45070E6DBEE", apid));
            if (!itc.exists()) {
                itc = new File(itc.getPath() + "2");
            }
            if (itc.exists()) {

System.err.println("ITC: " + name + ", " + artist + ", " + itc);
                found = true;
            } else {
                System.err.println(itc + " not exists: " + name + ", " + artist);
            }
        } else {
            System.err.println("apid not found: " + name + ", " + artist);
        }

        return found;
    }

    static String itc(String pid, String apid) {

        String sb = "/Users/nsano/Music/iTunes/Album Artwork/Download/" +
                pid +
                '/' +
                String.format("%02d", Integer.parseInt(apid.substring(15, 16), 16) & 0x0F) +
                '/' +
                String.format("%02d", Integer.parseInt(apid.substring(14, 15), 16) & 0x0F) +
                '/' +
                String.format("%02d", Integer.parseInt(apid.substring(13, 14), 16) & 0x0F) +
                '/' + pid + "-" + apid +
                ".itc";

        return sb;
    }
}
