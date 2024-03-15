/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.artwork;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;


/**
 * ITCBoxFactoryTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/08 nsano initial version <br>
 */
public class ITCBoxFactoryTest {

    /**
     * @param args iTunes artwork root directory
     */
    public static void main(String[] args) throws Exception {
        new ITCBoxFactoryTest(args[0]);
    }

    BufferedImage image;

    ITCBoxFactoryTest(String dir) throws IOException {
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

        AtomicInteger c = new AtomicInteger();
        Files.walk(Path.of(dir)).forEach(file -> {
            try {
                if (file.getFileName().toString().matches(".+\\.((itc)|(itc2))")) {
                    BoxFactory factory = BoxFactoryFactory.getFactory(ITCBoxFactory.class.getName());

                    InputStream is = Files.newInputStream(file);
                    Debug.println(c + ": " + file);
                    while (is.available() > 0) {
                        Box box = factory.getInstance(is);
                        Debug.println(box);
                        if (box instanceof item) {
                            try {
                                image = ImageIO.read(new ByteArrayInputStream(box.getData()));
                            } catch (Exception e) {
                                e.printStackTrace(System.err);
                            }
//if (c.get() < 100) {
// if (((item) box).imageHeight == 128) {
//  System.err.println(String.format("tmp/it/it_%02d.jpg", c.get()));
//  ImageIO.write(image, "JPG", new File(String.format("tmp/it/it_%02d.jpg", c.get())));
                            c.incrementAndGet();
// }
//}
                            frame.setTitle(file.toString());
                            panel.repaint();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }
}
