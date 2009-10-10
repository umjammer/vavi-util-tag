/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.artwork;

import java.awt.Graphics;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * iTunes Artwork
 *
 * <pre>
 *  itch
 *  item
 * </pre>
 * 
 * @see "http://www.waldoland.com/dev/Articles/ITCFileFormat.aspx"
 */
public class ITCBoxFactory implements BoxFactory {
    /** */
    public Box getInstance(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        long offset = dis.readInt();
        byte id[] = new byte[4];
        dis.readFully(id);
        if (offset == 1) {
            offset = dis.readLong();
Debug.println("64 bit length: " + offset);
        }

        Box box = null;
        String idString = new String(id);
        if ("itch".equals(idString)) {
            box = new itch();
        } else if ("item".equals(idString)) {
            box = new item();
        } else {
            box = new Box();
        }
        box.setFactory(this); // TODO bad!
        box.setOffset(offset);
        box.setId(id);
//Debug.println("id: " + new String(id) + ", length: " + offset + " (" + StringUtil.toHex16(offset) + ")");
        box.inject(dis);
        return box;
    }

    /** */
    public static void main(String[] args) throws Exception {
        new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                BoxFactory factory = BoxFactoryFactory.getFactory(ITCBoxFactory.class.getName());

                InputStream is = new FileInputStream(file);
                while (is.available() > 0) {
                    Box box = factory.getInstance(is);
Debug.println(box);
                    if (box instanceof item) {
                        final Image image = ImageIO.read(new ByteArrayInputStream(box.getData()));
                        JFrame frame = new JFrame();
                        frame.setTitle(file.getPath());
                        frame.setSize(600, 600);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        JPanel panel = new JPanel() {
                            public void paint(Graphics g) {
                                g.drawImage(image, 0, 0, this);
                            }
                        };
                        frame.getContentPane().add(panel);
                        frame.setVisible(true);
                    }
                }
            }
        }, Pattern.compile(".+\\.itc")).dig(new File(args[0]));
    }
}

/* */
