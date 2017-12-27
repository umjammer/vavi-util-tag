/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.box.Box;
import vavi.util.box.MetaFullBox;


/**
 * <pre>
 * /moov/trak/mdia/minf/stbl/stsd
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class stsd extends MetaFullBox {

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        super.injectBase(dis);
        int enties = dis.readInt();
        for (int i = 0; i < enties; i++) {
            Box subBox = factory.getInstance(dis);
            metaSupport.addSubBox(subBox);
        }
    }
}

/* */
