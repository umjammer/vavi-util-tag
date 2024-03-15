/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import vavi.util.StringUtil;
import vavi.util.tag.id3.CharConverter;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * BinaryWithDescriptionFrameContent.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class BinaryWithDescriptionFrameContent extends BinaryFrameContent {
    /** jdk1.4 logger */
//  private static Logger logger = Logger.getLogger(BinaryWithDescriptionFrameContent.class.getName());

    /** */
    private String description;

    /** */
    public BinaryWithDescriptionFrameContent() {
    }

    /** */
    public BinaryWithDescriptionFrameContent(byte[] content) {
        int i = 0;
        while (i < content.length) {
            if (content[i] == 0) {
                if (i + 1 < content.length && content[i + 1] == 0) {
                    i++;
                }
                break;
            }
            i++;
        }
        // Japanese Special!!!
        description = CharConverter.createString(content, 0, i);
        byte[] binary = new byte[content.length - i];
        System.arraycopy(content, i, binary, 0, content.length - i);
        this.content = binary;
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        // check correct format
        if (description == null || content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, description.length() + 1 + ((byte[]) content).length);

        build.put(description);
        build.put((byte) 0);
        build.put((byte[]) content);

        return build.getBytes();
    }

    /** */
    public String toString() {
//        return "binary: " + description + ": " + (((byte[]) content).length) + " bytes";
        if (content == null) {
            return null;
        } else {
            if ((((byte[]) content).length < 3) || (((byte[]) content).length > 1024)) {
                return "binary: " + description + ": " + (((byte[]) content).length) + " bytes" + "\n" + StringUtil.getDump((byte[]) content, 64);
            } else {
                return "binary: " + description + ": " + (((byte[]) content).length) + " bytes" + "\n" + StringUtil.getDump((byte[]) content, 64) + CharConverter.createString2((byte[]) content, 0, ((byte[]) content).length);
            }
        }
    }
}
