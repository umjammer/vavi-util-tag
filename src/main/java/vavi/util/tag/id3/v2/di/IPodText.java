/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.util.logging.Logger;

import vavi.util.tag.id3.CharConverter;


/**
 * IPodText. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class IPodText implements FrameText {

    /** jdk1.4 logger */
    private static final Logger logger = Logger.getLogger(IPodText.class.getName());

    /** iTunNORM */
    public String getText(byte[] content, int start, String encoding) {
logger.info("iTunNORM:");
        int length = Math.max(content.length - start - Util.getLastZeros(content, 1), 0);
        return CharConverter.createString(content, start, length);
    }
}

/* */
