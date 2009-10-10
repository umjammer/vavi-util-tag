/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag;

import java.io.IOException;


/**
 * Tag. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051208 nsano initial version <br>
 */
public interface Tag {
    /** */
    public Object getTag(String key) throws TagException;

    /** */
    public void setTag(String key, Object value) throws TagException;

    /** */
    public void update() throws IOException;
}

/* */
