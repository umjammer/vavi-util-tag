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
 * Name of the artist.
 *
 * <pre>
 * /moov/udta/meta/ilst/©ART
 *                          /data
 * </pre>
 *
 * TODO make super class
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/04 umjammer initial version <br>
 */
public class _ART extends MetaBox {

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
        return "id = ©ART, data:\n" + StringUtil.getDump(getData());
    }
}
