/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.box.FullBox;


/**
 * <pre>
 * /moov/trak/mdia/hdlr
 * </pre>
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class hdlr extends FullBox {

    byte[] componentType = new byte[4];

    byte[] componentSubType = new byte[4];

    int componentManufacture;
    int componentFlags;
    int componentFlagsMask;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        super.injectBase(dis);
        dis.readFully(this.componentType);
        dis.readFully(this.componentSubType);
        this.componentManufacture = dis.readInt();
        this.componentFlags = dis.readInt();
        this.componentFlagsMask = dis.readInt();
        dis.skipBytes((int) offset - 8 - 24); // TODO 64 bit
    }

    /* */
    public String toString() {
        return String.format("hdlr: componentType: %s, componentSubType: %s, componentManufacture: %d, componentFlags: %d, componentFlagsMask: %d\n",
                             new String(componentType),
                             new String(componentSubType),
                             componentManufacture,
                             componentFlags,
                             componentFlagsMask);
    }
}

/* */
