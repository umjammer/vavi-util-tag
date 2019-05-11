/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.artwork;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.jupiter.api.Disabled;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;
import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * ITCBoxFactoryTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/08 nsano initial version <br>
 */
@Disabled
public class ITCBoxFactoryTest {

    /** */
    public static void main(String[] args) throws Exception {
        new ITCBoxFactoryTest(args[0]);
    }

    BufferedImage image;

    ITCBoxFactoryTest(String dir) throws IOException {
        final JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        final JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        frame.getContentPane().add(panel);
        frame.setVisible(true);

        new RegexFileDigger(new FileDigger.FileDredger() {
//int c = 0;
            public void dredge(File file) throws IOException {
                BoxFactory factory = BoxFactoryFactory.getFactory(ITCBoxFactory.class.getName());

                InputStream is = new FileInputStream(file);
Debug.println(file);
                while (is.available() > 0) {
                    Box box = factory.getInstance(is);
Debug.println(box);
                    if (box instanceof item) {
                        try {
                            image = ImageIO.read(new ByteArrayInputStream(box.getData()));
                        } catch (Exception e) {
                            e.printStackTrace(System.err);
                        }
//if (c < 100) {
// if (((item) box).imageHeight == 128) {
//  System.err.println(String.format("tmp/it_%02d.jpg", c));
//  ImageIO.write(image, "JPG", new File(String.format("tmp/it_%02d.jpg", c)));
//  c++;
// }
//}
                        frame.setTitle(file.getPath());
                        panel.repaint();
                    }
                }
            }
        }, Pattern.compile(".+\\.((itc)|(itc2))")).dig(new File(dir));
    }
}

/* */
