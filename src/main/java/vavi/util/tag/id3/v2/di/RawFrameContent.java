/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * RawFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class RawFrameContent extends FrameContent {

    /** jdk1.4 logger */
//  private static Logger logger = Logger.getLogger(RawFrameContent.class.getName());

    /**
     * Constructor.
     */
    public RawFrameContent() {
    }

    /**
     * Constructor.
     */
    public RawFrameContent(byte[] content) {
        this.content = content;
    }


    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        // check correct format
        if (content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, ((byte[]) content).length);

        build.put((byte[]) content);

        return build.getBytes();
    }

    /** */
    public String toString() {
        return content == null ? null : "raw: " + (((byte[]) content).length) + " bytes";
    }

    // EncapsulatedObject
//    void encapsel() {
//        if ((obj.getType() == null) || (obj.getTextSubtype() == null) || (obj.getDescription() == null) || (obj.getContent() == null)) {
//            throw new TagFormatException();
//        }
//
//        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6 + obj.getType().length() + (obj.getTextSubtype().length() * 2) + (obj.getDescription().length() * 2) + obj.getContent().length);
//
//        try {
//            build.put(obj.getType().getBytes(System.getProperty("file.encoding")));
//        } catch (UnsupportedEncodingException e) {
//            throw new TagFormatException(e);
//        }
//        build.put((byte) 0);
//        build.put(obj.getTextSubtype());
//        build.put((byte) 0);
//        build.put((byte) 0);
//        build.put(obj.getDescription());
//        build.put((byte) 0);
//        build.put((byte) 0);
//        build.put(obj.getContent());
//    }
}

/* */
