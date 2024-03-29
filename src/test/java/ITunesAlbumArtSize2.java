/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.imageio.ImageIO;

import org.kafsemo.titl.Library;
import org.kafsemo.titl.ParseLibrary;
import org.kafsemo.titl.Track;
import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;
import vavi.util.itunes.artwork.ITCBoxFactory;
import vavi.util.itunes.artwork.item;
import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;
import vavi.util.tag.id3.v2.impl.ID3v2FrameV230;
import vavi.util.tag.mp4.MP4File;
import vavi.util.tag.mp4.MP4Tag;


/**
 * ITunesAlbumArtSize2. (album art size by directory)
 *
 * DOESN'T WORK: iTunes is replaced by Music.app
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 120613 nsano initial version <br>
 */
public class ITunesAlbumArtSize2 {

    static class ApidDao {
        Connection connection;
        ApidDao() {
            try {
                Files.createDirectories(Path.of("tmp"));

                connection = DriverManager.getConnection("jdbc:hsqldb:file:tmp/apiddb", "SA", "");

                Statement statement = connection.createStatement();
                statement.execute("DROP TABLE apid IF EXISTS;");
                statement.execute("CREATE TABLE apid(name VARCHAR, artist VARCHAR, apid VARCHAR);");
                statement.close();

                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO apid VALUES (?, ?, ?);");

                Library lib = ParseLibrary.parse(new File(System.getProperty("user.home") + "/Music/iTunes/iTunes Library.itl"));
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
     * @param args top_directory regex_pattern
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
                        boolean found = exec9_2(file);
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

    static List<String> queue = new ArrayList<>();

    /** */
    private static boolean exec9_2(Path file) throws Exception {
        boolean found;
        queue.clear();
        if (file.getFileName().toString().toLowerCase().endsWith(".mp3")) {
            found = exec9_3(file);
        } else {
            found = exec9_4(file);
        }
        if (found) {
            System.out.print(file.getParent() + "\t");
            System.out.print(file.getFileName().toString().substring(file.getFileName().toString().lastIndexOf('.') + 1).toLowerCase() + "\t");
            for (String s : queue) {
                System.out.print(s);
            }
            System.out.println();
        }
        return found;
    }

    /**
     * @param file m4a
     */
    private static boolean exec9_4(Path file) throws Exception {

        boolean found = false;

        MP4File mp4File = new MP4File(file.toAbsolutePath().toString());

        MP4Tag tag = (MP4Tag) mp4File.getTag();
        Iterator<?> i = tag.tags();
        while (i.hasNext()) {
            Box box = (Box) i.next();
            String key = new String(box.getId());
            if (key.equals("covr")) {
                ByteArrayInputStream is = new ByteArrayInputStream(box.getData());
                BufferedImage image = ImageIO.read(is);
                queue.add(image.getWidth() + "x" + image.getHeight() + " ");
                found = true;
            }
        }

        if (!found) {
//            System.err.println(List.class.cast(tag.getTag((char) 0xa9 + "nam")).get(0).getClass().getName() + ", " + List.class.cast(tag.getTag((char) 0xa9 + "ART")).get(0).getClass().getName());
            String name = new String(((Box) ((List<?>) tag.getTag((char) 0xa9 + "nam")).get(0)).getData()).substring(8);
            String artist = new String(((Box) ((List<?>) tag.getTag((char) 0xa9 + "ART")).get(0)).getData()).substring(8);
            found = itcImage(file, name, artist);
        }

        return found;
    }

    static BoxFactory itcFactory = BoxFactoryFactory.getFactory(ITCBoxFactory.class.getName());

    /**
     * @param file mp3
     */
    private static boolean exec9_3(Path file) throws Exception {

        boolean found = false;

        MP3File mp3File = new MP3File(file.toAbsolutePath().toString());

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (key.equals("APIC")) {
                    FrameContent frameContent = ((ID3v2FrameV230) frame).getContent();
                    BufferedImage image = (BufferedImage) frameContent.getContent();
                    queue.add(image.getWidth() + "x" + image.getHeight() + " ");
                    found = true;
                }
            }

            if (!found) {
                String name = (String) tag.getTag("Title");
                String artist = (String) tag.getTag("Artist");
                found = itcImage(file, name, artist);
            }
        } else {
            Debug.println("no id3v2 tag");
        }

        return found;
    }

    static boolean itcImage(Path file, String name, String artist) throws IOException {
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
                InputStream is = Files.newInputStream(itc.toPath());
                while (is.available() > 0) {
                    Box box = itcFactory.getInstance(is);
                    if (box instanceof item) {
                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(box.getData()));
                        queue.add(image.getWidth() + "x" + image.getHeight() + " ");
                        found = true;
                    }
                }
            } else {
                System.err.println(itc + " not exists: " + name + ", " + artist);
            }
            if (found) {
                queue.add("\t*");
            }
        } else {
            System.err.println("apid not found: " + name + ", " + artist);
        }

        return found;
    }

    static String itc(String pid, String apid) {

        String sb = System.getProperty("user.home") + "/Music/iTunes/Album Artwork/Download/" +
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
