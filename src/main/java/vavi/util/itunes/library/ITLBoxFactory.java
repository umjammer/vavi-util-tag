/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.library;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;

import vavi.util.box.Box;
import vavi.util.box.BoxFactory;

import static java.lang.System.getLogger;


/**
 * iTunes Library
 *
 * <pre>
 * </pre>
 *
 * @see ""
 */
public class ITLBoxFactory implements BoxFactory {

    private static final Logger logger = getLogger(ITLBoxFactory.class.getName());

    boolean first = true;

    /** */
    public Box getInstance(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        byte[] id = new byte[4];
        dis.readFully(id);
        long offset = dis.readInt();
        if (offset == 1) {
            offset = dis.readLong();
logger.log(Level.TRACE, "64 bit length: " + offset);
        }

        Box box = null;
        String idString = new String(id);
logger.log(Level.TRACE, "id: " + new String(id) + ", length: " + offset + " (" + Long.toHexString(offset) + ")");
        if ("hdfm".equals(idString) && first) {
            box = new hdfm();
            first = false;
        } else {
            box = new Box();
        }
        box.setFactory(this); // TODO bad!
        box.setOffset(offset);
        box.setId(id);
//logger.log(Level.TRACE, "id: " + new String(id) + ", length: " + offset + " (" + Long.toHexString(offset) + ")");
        box.inject(dis);
        return box;
    }
}
