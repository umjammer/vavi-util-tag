/*
 * TextFrameEncoding.java
 $Id: TextFrameEncoding.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $

 de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
 Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Library General Public
 License as published by the Free Software Foundation; either
 version 2 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Library General Public License for more details.

 You should have received a copy of the GNU Library General Public
 License along with this library; if not, write to the
 Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 Boston, MA  02111-1307, USA.
*/

package vavi.util.tag.id3.v2.di;

import java.io.UnsupportedEncodingException;

import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * EncodedTextFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class EncodedTextFrameContent extends TextFrameContent {

    /** */
//    private static Logger logger = Logger.getLogger(EncodedTextFrameContent.class.getName());

    /** */
    protected String encoding;

    /** */
    protected String language;

    /** */
    public EncodedTextFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public EncodedTextFrameContent(byte[] content) {
        this.encoding = toJavaEncoding(content[0]);
        if (content[0] == 0) {
            // Japanese Special!!!
            FrameText frameText = FrameText.Factory.getFrameText("default.iso", null);
            this.content = frameText.getText(content, 1, encoding);
        } else {
            FrameText frameText = FrameText.Factory.getFrameText("default.unicode", null);
            this.content = frameText.getText(content, 1, encoding);
        }
//logger.info("encoding: " + encoding + ", text: " + this.content + "\n" + StringUtil.getDump(content));
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        // check correct format
        if (content == null) {
            throw new ID3v2Exception("tag format");
        }

        try {
            ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, ((String) content).getBytes(encoding).length);

            build.put(((String) content).getBytes(encoding));

            return build.getBytes();
        } catch (UnsupportedEncodingException e) {
            assert false : e.getMessage();
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

/* */
