/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3;

import vavi.util.tag.Tag;


/**
 * ID3Tag. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051208 nsano initial version <br>
 */
public interface ID3Tag extends Tag {

    /** */
    enum Type {
        ID3v1,
        ID3v2,
        RIFF
    }
}

/* */
