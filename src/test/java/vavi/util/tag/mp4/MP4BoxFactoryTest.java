/*
 * Copyright (c) 2014 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;

import static org.junit.jupiter.api.Assertions.*;


/**
 * MP4BoxFactoryTest. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2014/08/06 umjammer initial version <br>
 */
public class MP4BoxFactoryTest {

    @Test
    @Disabled
    public void test() {
        fail("Not yet implemented");
    }

    /** */
    public static void main(String[] args) throws Exception {
        BoxFactory factory = BoxFactoryFactory.getFactory(MP4BoxFactory.class.getName());
        InputStream is = Files.newInputStream(Paths.get(args[0]));
        while (is.available() > 0) {
            Box box = factory.getInstance(is);
Debug.println(box);
        }
    }
}

/* */
