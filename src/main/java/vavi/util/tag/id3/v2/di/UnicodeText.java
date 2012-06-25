/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.io.UnsupportedEncodingException;


/**
 * UnicodeText. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class UnicodeText implements FrameText {

    /** */
    public String getText(byte[] content, int start, String encoding) {
        try {
            int lastZeros = encoding.equalsIgnoreCase("UTF-16") ? 0 : Util.getLastZeros(content, 2);
            int length = Math.max(content.length - start - lastZeros, 0);
            return new String(content, start, length, encoding);
        } catch (UnsupportedEncodingException e) {
            assert false;
            return null;
        }
    }
}

/* */
