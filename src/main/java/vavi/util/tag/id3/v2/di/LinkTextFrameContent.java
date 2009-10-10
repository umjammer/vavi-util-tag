/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.util.logging.Logger;


/**
 * LinkTextFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class LinkTextFrameContent extends TextFrameContent {

    /** */
    private static Logger logger = Logger.getLogger(LinkTextFrameContent.class.getName());

    /** */
    protected String id;

    /** */
    private String description;

    /** */
    public LinkTextFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public LinkTextFrameContent(byte[] content) {
        this.id = new String(content, 0, 3);
        int i = 3;
        while (i < content.length) {
            if (content[i] == 0) {
                if (i + 1 < content.length && content[i + 1] == 0) {
                    i++;
                }
                break;
            }
            i++;
        }
        this.description = new String(content, 3, i);
        this.content = new String(content, i, content.length - i);
logger.info("id: " + id + ", description: " + description + ", text: " + content);
    }

    /**
     * TODO implement!
     */
    public void setContent(Object content) {
        if (!(content instanceof String)) {
            throw new IllegalArgumentException("not String: " + content.getClass());
        }
    }
}

/* */
