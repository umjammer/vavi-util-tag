/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.artwork;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.box.Box;


/**
 * itch. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class itch extends Box {

    /** */
    int data1;
    int data2;
    int data3;
    int data4;
    // artw
    byte[] indicator = new byte[4];

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        this.data1 = dis.readInt();
        this.data2 = dis.readInt();
        this.data3 = dis.readInt();
        this.data4 = dis.readInt();
        dis.readFully(this.indicator);
        dis.skipBytes((int) offset - 8 - 20); // TODO 64 bit
    }
}

/* */
