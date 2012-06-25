/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.library;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.Debug;
import vavi.util.StringUtil;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;


/**
 * iTunes Library
 *
 * <pre>
 * </pre>
 * 
 * @see ""
 */
public class ITLBoxFactory implements BoxFactory {
    boolean first = true;
    /** */
    public Box getInstance(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        byte[] id = new byte[4];
        dis.readFully(id);
        long offset = dis.readInt();
        if (offset == 1) {
            offset = dis.readLong();
Debug.println("64 bit length: " + offset);
        }

        Box box = null;
        String idString = new String(id);
Debug.println("id: " + new String(id) + ", length: " + offset + " (" + StringUtil.toHex16(offset) + ")");
        if ("hdfm".equals(idString) && first) {
            box = new hdfm();
            first = false;
        } else {
            box = new Box();
        }
        box.setFactory(this); // TODO bad!
        box.setOffset(offset);
        box.setId(id);
//Debug.println("id: " + new String(id) + ", length: " + offset + " (" + StringUtil.toHex16(offset) + ")");
        box.inject(dis);
        return box;
    }
}

/* */
