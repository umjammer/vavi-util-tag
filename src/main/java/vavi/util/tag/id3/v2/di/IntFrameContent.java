/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.util.logging.Logger;


/**
 * IntFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060102 nsano initial version <br>
 */
public class IntFrameContent extends EncodedTextFrameContent {

    /** */
    private static Logger logger = Logger.getLogger(IntFrameContent.class.getName());

    /** @see #setContent(Object) */
    public IntFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public IntFrameContent(byte[] content) {
        super(content);

        try {
            this.content = Integer.parseInt((String) this.content);
        } catch (NumberFormatException e) {
            logger.warning(e.toString());
        }
    }

    /**
     * TODO implement!
     */
    public void setContent(Object content) {
        if (content instanceof String) {
            // TODO そのまま
        } if (content instanceof Integer) {
            // TODO
        } else {
            throw new IllegalArgumentException("unhandled class: " + content.getClass());
        }
    }
}

/* */
