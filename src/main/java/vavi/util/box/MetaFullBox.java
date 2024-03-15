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
 * MetaFullBox.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class MetaFullBox extends FullBox implements Meta {

    /** */
    protected Support metaSupport = new Support();

    /** */
    public List<Box> getSubBoxes() {
        return metaSupport.getSubBoxes();
    }

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        super.injectBase(dis);
        metaSupport.inject(dis, offset - 8 - 4, factory);
    }

    /* */
    public String toString() {
        return metaSupport.toString(new String(id));
    }
}
