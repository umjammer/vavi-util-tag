/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.box.Box;


/**
 * <pre>
 * /moov/trak/mdia/mdhd
 * </pre>
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class mdhd extends Box {

    /** */
    int version;
    
    byte[] flags = new byte[3];

    int creationTime;
    int modificationTime;
    int timeScale;
    int duration;
    int language;
    int quality;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        this.version = dis.readUnsignedByte();
        dis.readFully(this.flags);
        this.creationTime = dis.readInt();
        this.modificationTime = dis.readInt();
        this.timeScale = dis.readInt();
        this.duration = dis.readInt();
        this.language = dis.readUnsignedShort();
        this.quality = dis.readUnsignedShort();
    }

    /* */
    public String toString() {
        return String.format("mdhd: creationTime: %1$tF %1$tT, modificationTime: %2$tF %2$tT, timeScale: %3$d, duration: %4$d, language: %5$d, quality: %6$d\n",
                             qtTimeToLong(creationTime),
                             qtTimeToLong(modificationTime),
                             timeScale,
                             duration,
                             language,
                             quality);
    }
}

/* */
