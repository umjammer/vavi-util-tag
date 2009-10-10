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
 * tkhd. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class tkhd extends Box {

    /** */
    int version;
    
    byte[] flags = new byte[3];

    int creationTime;
    int modificationTime;
    int trackId;
    byte[] reserved1 = new byte[4];
    int duration;
    byte[] reserved2 = new byte[8];
    int layer;
    int alternateGroup;
    int volume;
    byte[] reserved3 = new byte[2];
    byte[] matrixStructure = new byte[36];
    float trackWidth;
    float trackHeight;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        this.version = dis.readUnsignedByte();
        dis.readFully(this.flags);
        this.creationTime = dis.readInt() & 0xffffffff;
        this.modificationTime = dis.readInt() & 0xffffffff;
        this.trackId = dis.readInt();
        dis.readFully(this.reserved1);
        this.duration = dis.readInt();
        dis.readFully(this.reserved2);
        this.layer = dis.readUnsignedShort();
        this.alternateGroup = dis.readUnsignedShort();
        this.volume = dis.readUnsignedShort();
        dis.readFully(this.reserved3);
        dis.readFully(this.matrixStructure);
        this.trackWidth = dis.readFloat();
        this.trackHeight = dis.readFloat();
    }

    /* */
    public String toString() {
        return String.format("tkhd: creationTime: %1$tF %1$tT, modificationTime: %2$tF %2$tT, trackId: %3$d, duration: %4$d, trackWidth: %5$f, trackHeight: %6$f\n",
                             qtTimeToLong(creationTime),
                             qtTimeToLong(modificationTime),
                             trackId,
                             duration,
                             trackWidth,
                             trackHeight);
    }
}

/* */
