// TagContent.java
// $Id: TagContent.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
//
// de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
// Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the
// Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA  02111-1307, USA.

package vavi.util.tag.id3.v2;

import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * An instance of this class contains the content read from a
 * ID3(v2) tag. This class is designed to be as flexible as possible
 * to reduce the number of cases where information has to be returned
 * as binary when it is rather more structured.
 * <p>
 * It provides storage for
 * <li> a type (e.g. a MIME-type or a language, Text)
 * <li> a subtype (text or binary)
 * <li> a description (text)
 * <li> the content (text or binary)
 * <p>
 * Unused fields should be set to <code>null</code>.
 */
public abstract class FrameContent {

    /** jdk1.4 logger */
    private static final Logger logger = Logger.getLogger(FrameContent.class.getName());

    /** */
    protected Object content;

    /** */
    protected static String defaultEncoding;

    static {
        try {
            Properties props = new Properties();
            props.load(FrameContent.class.getResourceAsStream("/vavi/util/tag/id3/id3.properties"));
            defaultEncoding = props.getProperty("id3.encoding");
        } catch (IOException e) {
            logger.severe(e.toString());
        }
    }

    /**
     * Set content field with binary data
     *
     * @param content Content to set
     */
    public void setContent(Object content) {
        this.content = content;
    }

    /**
     * Get content
     *
     * @return Binary content
     */
    public Object getContent() {
        return content;
    }

    /** */
    public abstract byte[] toByteArray() throws ID3v2Exception;

    /**
     * @param code first byte of text
     * @return java encoding string
     */
    protected static String toJavaEncoding(int code) {
//logger.info("code: " + code);
        return switch (code) {
            default -> "ISO-8859-1";
            case 1 -> "UTF-16";
            case 2 -> "UTF-16BE";
            case 3 -> "UTF-8";
        };
    }
}

/* */
