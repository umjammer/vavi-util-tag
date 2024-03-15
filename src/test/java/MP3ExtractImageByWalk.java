/*
 * Copyright (c) 2020 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavi.util.tag.id3.ID3Tag.Type;
import vavi.util.tag.id3.MP3File;
import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2;
import vavi.util.tag.id3.v2.ID3v2Frame;


/**
 * MP3ExtractImageByWalk. (mp3 extract image by directory)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 200217 nsano initial version <br>
 */
public class MP3ExtractImageByWalk {

    static Logger logger = Logger.getLogger(MP3ExtractImageByWalk.class.getName());

    /**
     * @param args 0: top_directory, 1: regex_pattern
     */
    public static void main(String[] args) throws Exception {
        MP3ExtractImageByWalk app = new MP3ExtractImageByWalk();
        app.exec(args);
    }

    JFrame frame;
    JPanel panel;
    BufferedImage image;

    /** */
    private void exec(String[] args) throws Exception {
        frame = new JFrame();
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        Files.walk(Path.of(args[0])).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(args[1])) {
                    exec(file.toAbsolutePath().toString());
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    /**
     * @param file mp3, m4a
     */
    private void exec(String file) throws Exception {
        if (file.toLowerCase().endsWith(".mp3")) {
            exec_mp3(file);
        } else {
            exec_m4a(file);
        }
    }

    private void exec_mp3(String mod) throws Exception {
        MP3File mp3File = new MP3File(mod);

        if (mp3File.hasTag(Type.ID3v2)) {
            ID3v2 tag = (ID3v2) mp3File.getTag(Type.ID3v2);
            Iterator<?> i = tag.tags();
            int c = 0;
            while (i.hasNext()) {
                ID3v2Frame frame = (ID3v2Frame) i.next();
                String key = frame.getID();
                if (key.equals("APIC")) {
                    FrameContent content = frame.getContent(key);
                    BufferedImage image = (BufferedImage) content.getContent();
                    if (image != null) {
                        printFrame(mod, image, c);
                    }
                    c++;
                }
            }
        }
    }

    private void exec_m4a(String mod) throws Exception {

    }

    void printFrame(String mod, BufferedImage data, int count) {
        image = data;
        String name = mod.substring(mod.lastIndexOf("/") + 1, mod.lastIndexOf("."));
        if (count > 0) {
            name += " (" + count + ")";
        }
System.err.println(name);
try {
    ImageIO.write(image, "PNG", new File(String.format("tmp/aw/%s.png", name)));
} catch (IOException e) {
    e.printStackTrace();
}
        frame.setTitle(mod);
        panel.repaint();
    }
}
