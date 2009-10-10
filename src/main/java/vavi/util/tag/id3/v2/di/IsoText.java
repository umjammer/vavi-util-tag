/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import vavi.util.tag.id3.CharConverter;


/**
 * IsoText. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class IsoText implements FrameText {

    /** */
    public String getText(byte[] content, int start, String encoding) {
        int length = Math.max(content.length - start - Util.getLastZeros(content, 1), 0);
        return CharConverter.createString(content, start, length);
    }
}

/* */
