/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.io.UnsupportedEncodingException;

import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * EncodedTextWithLanguageAndDescriptionFrameContent.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class EncodedTextWithLanguageAndDescriptionFrameContent extends EncodedTextWithLanguageFrameContent {

    /** */
//  private static Logger logger = Logger.getLogger(EncodedTextWithLanguageAndDescriptionFrameContent.class.getName());

    /** */
    private String description;

    /** */
    public EncodedTextWithLanguageAndDescriptionFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public EncodedTextWithLanguageAndDescriptionFrameContent(byte[] content) {
        this.encoding = toJavaEncoding(content[0]);
        this.language = new String(content, 1, 3);
        int i = 4;
        while (i < content.length) {
            if (content[i] == 0) {
                break;
            }
            i++;
        }
        try {
            this.description = new String(content, 4, i - 4, encoding);
        } catch (UnsupportedEncodingException e) {
            this.description = new String(content, 4, i - 4);
        }
        if (content[0] == 0) {
            // Japanese Special!!!
            FrameText frameText = FrameText.Factory.getFrameText(description, "default.iso");
            this.content = frameText.getText(content, i + 1, encoding);
        } else {
            FrameText frameText = FrameText.Factory.getFrameText(description, "default.unicode");
            this.content = frameText.getText(content, i + 2, encoding);
        }
//logger.info("encoding: " + encoding + ", language: " + language + ", description: " + description + ", text: " + this.content + "\n" + StringUtil.getDump(content));
    }

    /** TODO */
    public byte[] toByteArray() throws ID3v2Exception {
        if (language == null || description == null || content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6 + (description.length() * 2) + (((String) content).length() * 2));

        build.put(language);
        build.put(description);
        build.put((byte) 0);
        build.put((byte) 0);
        build.put((String) content);

        return build.getBytes();
    }

    /** */
    public void setContent(Object content) {
        // TODO implement!
        if (!(content instanceof String)) {
            throw new IllegalArgumentException("not String: " + content.getClass());
        }
    }
}

/* */
