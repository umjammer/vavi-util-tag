/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.artwork;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;


/**
 * iTunes Artwork
 *
 * <pre>
 *  itch
 *  item
 * </pre>
 *
 * @see "http://www.waldoland.com/dev/Articles/ITCFileFormat.aspx"
 */
public class ITCBoxFactory implements BoxFactory {
    /** */
    public Box getInstance(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        long offset = dis.readInt();
        byte[] id = new byte[4];
        dis.readFully(id);
        if (offset == 1) {
            offset = dis.readLong();
Debug.println("64 bit length: " + offset);
        }

        Box box = null;
        String idString = new String(id);
//Debug.println("id: " + new String(id) + ", length: " + offset + " (" + StringUtil.toHex16(offset) + ")");
        if ("itch".equals(idString)) {
            box = new itch();
        } else if ("item".equals(idString)) {
            box = new item();
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
