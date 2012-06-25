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
 * /udta/meta/ilst/----
 *                     /mean 
 *                     /name 
 *                     /data 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/04 umjammer initial version <br>
 */
public class ____ extends MetaBox {

    /**
     * <pre>
     * "com.apple.iTunes"
     * </pre>
     */
    public String getMean() {
        Box box = metaSupport.getSubBox("mean");
        return new String(box.getData());
    }

    /**
     * <pre>
     * iTunSMPB                 hex strings
     * Encoding Params          text binary, ...
     * iTunNORM                 hex strings
     * iTunes_CDDB_1            URL encoded text
     * iTunes_CDDB_TrackNumber  decimal string
     * </pre>
     */
    public String getName() {
        Box box = metaSupport.getSubBox("name");
        byte[] data = box.getData();
        return new String(data, 4, data.length - 4); // TODO fixed value 4
    }

    /**
     * TODO returns another meaning value
     * @return sub box "data"'s data
     */
    public byte[] getData() {
        Box box = metaSupport.getSubBox("data");
        return box.getData();
    }

    /* */
    public String toString() {
        return "id = ----, mean: " + getMean() + ", name: " + getName() + ", data:\n" + StringUtil.getDump(getData());
    }
}

/* */
