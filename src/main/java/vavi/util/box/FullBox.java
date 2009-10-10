/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.box;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.StringUtil;


/**
 * FullBox. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public class FullBox extends Box {
    /** */
    protected int version;
    
    protected byte[] flags = new byte[3];

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        injectBase(dis);
    }

    /** */
    protected void injectBase(DataInputStream dis) throws IOException {
        this.version = dis.readUnsignedByte();
        dis.readFully(this.flags);
    }

    /* */
    public String toString() {
        return "id: " + new String(id) + ", offset: " + offset + ", version: " + version + (data != null ? "\n" + StringUtil.getDump(data, 128) : "\n");
    }
}

/* */
