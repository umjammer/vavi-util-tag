/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import org.mozilla.universalchardet.UniversalDetector;


/**
 * CharConverter. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051208 nsano initial version <br>
 */
public final class CharConverter {

    /** */
    private static Logger logger = Logger.getLogger(CharConverter.class.getName());

    /** */
    private CharConverter() {
    }

    /** */
    public static String createString2(byte[] buffer, int start, int length) {
        String value = null;
        try {
            UniversalDetector detector = new UniversalDetector(null);
            detector.handleData(buffer, start, length);
            detector.dataEnd();

            String encoding = detector.getDetectedCharset();
            if (encoding != null) {
                value = new String(buffer, start, length, encoding);
logger.fine("VAVI: ENCODING: " + encoding);
            } else {
                value = new String(buffer, start, length, CharConverter.encoding);
logger.info("VAVI: ENCODING: unknown, use " + CharConverter.encoding);
            }

            detector.reset();
        } catch (Exception e) {
            value = new String(buffer, start, length);
logger.severe("VAVI: ENCODING: unknown, use " + System.getProperty("file.encoding") + ": " + e);
        }
        int p = value.indexOf(0);
        if (p != -1) {
            value = value.substring(0, p);
        }
        return value;
    }

    /** */
    public static String createString(byte[] buffer, int start, int length) {
        String value = null;
        try {
            // Special!!!
            value = new String(buffer, start, length, encoding);
//logger.info("VAVI: ENCODING: " + encoding);
        } catch (Exception e) {
            try {
                value = new String(buffer, start, length, "UTF-16");
//logger.info("VAVI: ENCODING: unicode");
            } catch (Exception f) {
                value = new String(buffer, start, length);
//logger.info("VAVI: ENCODING: unknown");
            }
        }
//logger.info("\n" + StringUtil.getDump(buffer));
        int p = value.indexOf(0);
        if (p != -1) {
            value = value.substring(0, p);
        }
        return value;
    }

    /** */
    private static String encoding = System.getProperty("file.encoding");

    /** */
    static {
        try {
            Properties props = new Properties();
            props.load(CharConverter.class.getResourceAsStream("/vavi/util/tag/id3/id3.properties"));
            encoding = props.getProperty("id3.encoding");
        } catch (IOException e) {
            logger.warning(e.getStackTrace()[0].toString());
        }
    }
}

/* */
