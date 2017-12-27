/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * GenreUtil. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060102 nsano initial version <br>
 */
public final class GenreUtil {

    /** */
    private static Logger logger = Logger.getLogger(GenreUtil.class.getName());

    /** */
    private GenreUtil() {
    }

    /** */
    public static String getGenreString(int genreCode) {
        // The following genres are defined in ID3v1 
        return genres.getProperty(String.valueOf(genreCode), "");
    }

    /** */
    private static final Properties genres = new Properties();

    /** */
    static {
        try {
            genres.load(GenreUtil.class.getResourceAsStream("/vavi/util/tag/id3/genre.properties"));
        } catch (IOException e) {
            logger.warning(e.toString());
        }
    }    
}

/* */
