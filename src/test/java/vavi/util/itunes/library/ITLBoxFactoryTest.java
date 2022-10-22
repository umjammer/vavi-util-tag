/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.library;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.Disabled;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;


/**
 * ITLBoxFactoryTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/18 umjammer initial version <br>
 */
@Disabled
public class ITLBoxFactoryTest {

    /** */
    public static void main(String[] args) throws Exception {
        new ITLBoxFactoryTest(args[0]);
    }

    public ITLBoxFactoryTest(String file) throws IOException {
        BoxFactory factory = BoxFactoryFactory.getFactory(ITLBoxFactory.class.getName());
        InputStream is = Files.newInputStream(Paths.get(file));
        while (is.available() > 0) {
            Box box = factory.getInstance(is);
Debug.println(box);
        }
    }
}

/* */
