/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import vavi.util.StringUtil;
import vavi.util.box.Box;
import vavi.util.box.MetaBox;


/**
 * Cover (or other) artwork binary data.
 *
 * <pre>
 * /moov/udta/meta/ilst/covr
 *                          /data
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/13 umjammer initial version <br>
 */
public class covr extends MetaBox {

    /**
     * TODO returns another meaning value
     * @return sub box "data"'s data
     */
    public byte[] getData() {
        Box box = metaSupport.getSubBox("data");
        byte[] data = box.getData();
System.err.println(StringUtil.getDump(data, 64));
        final int O = 8;
        byte[] result = new byte[data.length - O];
        System.arraycopy(data, O, result, 0, data.length - O);
        return result;
    }

    /* */
    public String toString() {
        return "id = covr, data:\n" + StringUtil.getDump(getData(), 128);
    }
}
