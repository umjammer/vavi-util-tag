/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.io.UnsupportedEncodingException;

import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * EncodedTextWithLanguageFrameContent.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class EncodedTextWithLanguageFrameContent extends TextFrameContent {

    /** */
//    private static Logger logger = Logger.getLogger(EncodedTextWithLanguageFrameContent.class.getName());

    /** */
    protected String encoding;

    /** */
    protected String language;

    /** */
    public EncodedTextWithLanguageFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public EncodedTextWithLanguageFrameContent(byte[] content) {
        encoding = toJavaEncoding(content[0]);
        language = new String(content, 1, 3);
        if (content[0] == 0) {
            // Japanese Special!!!
            FrameText frameText = FrameText.Factory.getFrameText("default.iso", null);
            this.content = frameText.getText(content, 4, encoding);
        } else {
            FrameText frameText = FrameText.Factory.getFrameText("default.unicode", null);
            this.content = frameText.getText(content, 4, encoding);
        }
//logger.info("encoding: " + encoding + ", language: " + language + ", text: " + this.content + "\n" + StringUtil.getDump(content));
    }

    /** TODO */
    public byte[] toByteArray() throws ID3v2Exception {
        if (language == null || content == null) {
            throw new ID3v2Exception("tag format");
        }

        try {
            ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 4 + (((String) content).getBytes(encoding).length));

            build.put(language);
            build.put(((String) content).getBytes(encoding));

            return build.getBytes();
        } catch (UnsupportedEncodingException e) {
            assert false;
            return null;
        }
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
