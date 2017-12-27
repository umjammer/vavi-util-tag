/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.box;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;


/**
 * MetaBox.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class MetaBox extends Box implements Meta {
    /** */
    protected Support metaSupport = new Support();

    /** */
    public List<Box> getSubBoxes() {
        return metaSupport.getSubBoxes();
    }

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        metaSupport.inject(dis, offset - 8, factory);
    }

    /* */
    public String toString() {
        return metaSupport.toString(new String(id));
    }
}

/* */
