/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag;

import java.io.IOException;
import java.util.Iterator;


/**
 * Tag. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051208 nsano initial version <br>
 */
public interface Tag {
    /** */
    Object getTag(String key) throws TagException;

    /** */
    void setTag(String key, Object value) throws TagException;

    /** */
    void update() throws IOException;

    /** */
    Iterator<?> tags() throws TagException;
}

/* */
