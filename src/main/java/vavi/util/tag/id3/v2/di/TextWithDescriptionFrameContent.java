/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.io.UnsupportedEncodingException;

import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * TextWithDescriptionFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class TextWithDescriptionFrameContent extends TextFrameContent {

    /** */
//  private static Logger logger = Logger.getLogger(TextWithDescriptionFrameContent.class.getName());

    /** */
    protected String encoding;

    /** */
    private String description;

    /** */
    public TextWithDescriptionFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public TextWithDescriptionFrameContent(byte[] content) {
        this.encoding = toJavaEncoding(content[0]);
        int i = 1;
        while (i < content.length) {
            if (content[i] == 0) {
                break;
            }
            i++;
        }
        try {
            this.description = new String(content, 1, i - 1, encoding);
        } catch (UnsupportedEncodingException e) {
            this.description = new String(content, 1, i - 1);
        }
        if (content[0] == 0) {
            // Japanese Special!!!
            FrameText frameText = FrameText.Factory.getFrameText(description, "default.iso");
            this.content = frameText.getText(content, i + 1, encoding);
        } else {
            FrameText frameText = FrameText.Factory.getFrameText(description, "default.unicode");
            this.content = frameText.getText(content, i + 2, encoding);
        }
// logger.info("encoding: " + encoding + ", description: " + description + ",
// text: " + this.content + "\n" + StringUtil.getDump(content));
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        if (description == null || content == null) {
            throw new ID3v2Exception("description or content is null");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, (description.length() * 2) + 3 + ((String) content).length());

        build.put(description);
        build.put((byte) 0);
        build.put((byte) 0);
        try {
            build.put(((String) content).getBytes(System.getProperty("file.encoding")));
        } catch (UnsupportedEncodingException e) {
            throw new ID3v2Exception(e);
        }

        return build.getBytes();
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
