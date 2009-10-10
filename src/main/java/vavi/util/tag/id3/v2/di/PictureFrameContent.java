/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.util.logging.Logger;

import vavi.util.StringUtil;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * PictureFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class PictureFrameContent extends BinaryFrameContent {
    /** jdk1.4 logger */
    private static Logger logger = Logger.getLogger(PictureFrameContent.class.getName());

    enum Type {
        /** 32x32 pixels 'file icon' (PNG only) */ 
        $01,
        /** Other file icon */ 
        $02,
        /** Cover (front) */ 
        $03,
        /** Cover (back) */ 
        $04,
        /** Leaflet page */ 
        $05,
        /** Media (e.g. label side of CD) */ 
        $06,
        /** Lead artist/lead performer/soloist */
        $07,
        /** Artist/performer */
        $08,
        /** Conductor */
        $09,
        /** Band/Orchestra */ 
        $0A,
        /** Composer */
        $0B,
        /** Lyricist/text writer */ 
        $0C,
        /** Recording Location */
        $0D,
        /** During recording */
        $0E,
        /** During performance */
        $0F,
        /** Movie/video screen capture */ 
        $10,
        /** A bright coloured fish */
        $11,
        /** Illustration */
        $12,
        /** Band/artist logotype */
        $13,
        /** Publisher/Studio logotype */
        $14 
    }
    
    /** */
    private String encoding;

    /** */
    private String mimeType;

    /** */
    private Type pictureType;

    /** */
    private String description;

    /** */
    public PictureFrameContent() {
    }

    /** */
    public PictureFrameContent(byte[] content) {
        this.encoding = toJavaEncoding(content[0]);
        int i = 1;
        while (i < content.length) {
            if (content[i] == 0) {
                break;
            }
            i++;
        }
        this.mimeType = new String(content, 1, i - 1);
        this.pictureType = Type.values()[content[i + 1]];
        int j = i + 2;
        while (j < content.length) {
            if (content[j] == 0) {
                break;
            }
            j++;
        }
        this.description = new String(content, i + 2, j - 1);
        byte[] binary = new byte[content.length - ( + j)];
        System.arraycopy(content, i + 2, binary, 0, j - 1 - (i + 2));
        this.content = binary;
logger.info("encoding: " + encoding + ", mimeType: " + mimeType + ", description: " + description + ", pictureType: " + this.pictureType + "\n" + StringUtil.getDump(content, 128));
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        if (mimeType == null || pictureType == null || description == null || content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.UNICODE, 6 + mimeType.length() + 1 + (description.length() * 2) + ((byte[]) content).length);

        build.put((byte) 0);
        build.put(mimeType);
        build.put((byte) 0);
        build.put((byte) pictureType.ordinal());
        build.put(description);
        build.put((byte) 0);
        build.put((byte) 0);
        build.put((byte[]) content);

        return build.getBytes();
    }

    /** */
    public void getContent(Object content) {
        // TODO implement!
    }
}

/* */
