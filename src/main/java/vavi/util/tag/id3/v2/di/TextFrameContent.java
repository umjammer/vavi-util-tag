/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import vavi.util.tag.id3.CharConverter;
import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * TextFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class TextFrameContent extends FrameContent {

    /** */
//  private static Logger logger = Logger.getLogger(TextFrameContent.class.getName());

    /** */
    public TextFrameContent() {
    }

    /** */
    public TextFrameContent(String text) {
        this.content = text;
    }

    /**
     * Creates a new TextFrame with a given content
     */
    public TextFrameContent(byte[] content) {
        // Japanese Special!!!
        this.content = CharConverter.createString2(content, 0, content.length);
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        // check correct format
        if (content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, ((String) content).getBytes().length);

        build.put(((String) content).getBytes());

        return build.getBytes();
    }

    /** */
    public String toString() {
        return String.valueOf(content);
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
