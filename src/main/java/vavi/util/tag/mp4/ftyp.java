/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.StringUtil;
import vavi.util.box.Box;


/**
 * <pre>
 * /ftyp 
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class ftyp extends Box {

    byte[] majourBrand = new byte[4];

    /** */
    int minorVersion;
    
    byte[] compatibleBrands = new byte[4];

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        dis.readFully(this.majourBrand);
        this.minorVersion = dis.readInt();
        dis.readFully(this.compatibleBrands);

        int length = (int) offset - 8 - 12;
        byte[] data = new byte[length];
        dis.readFully(data);

        this.data = data;
    }

    /* */
    public String toString() {
        return "ftyp: majourBrand: " + new String(majourBrand) + ", minorVersion: " + minorVersion + ", compatibleBrands: " + new String(compatibleBrands) + "\n" + StringUtil.getDump(data);
    }
}

/* */
