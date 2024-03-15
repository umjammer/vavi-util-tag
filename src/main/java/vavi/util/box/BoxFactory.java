/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.box;

import java.io.IOException;
import java.io.InputStream;


/**
 * BoxFactory.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070920 nsano initial version <br>
 */
public interface BoxFactory {

    /** */
    Box getInstance(InputStream is) throws IOException;

    /** */
    class BoxFactoryFactory {
        /** */
        public static BoxFactory getFactory(String className) {
            try {
                return (BoxFactory) Class.forName(className).getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
