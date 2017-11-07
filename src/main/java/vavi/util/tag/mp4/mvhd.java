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
 * /moov/mvhd
 * </pre>
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class mvhd extends Box {

    /** */
    int version;

    byte[] flags = new byte[3];

    int creationTime;
    int modificationTime;
    int timeScale;
    int duration;
    int preferredRate;
    int preferredVolume;

    byte[] reserved = new byte[10];
    byte[] matrixStructure = new byte[36];

    int previewTime;
    int previewDulation;
    int posterTime;
    int selectionTime;
    int selectionDulation;
    int currentTime;
    int nextTrackId;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        this.version = dis.readUnsignedByte();
        dis.readFully(this.flags);
        this.creationTime = dis.readInt();
        this.modificationTime = dis.readInt();
        this.timeScale = dis.readInt();
        this.duration = dis.readInt();
        this.preferredRate = dis.readInt();
        this.preferredVolume = dis.readUnsignedShort();
        dis.readFully(this.reserved);
        dis.readFully(this.matrixStructure);
        this.previewTime = dis.readInt();
        this.previewDulation = dis.readInt();
        this.posterTime = dis.readInt();
        this.selectionTime = dis.readInt();
        this.selectionDulation = dis.readInt();
        this.currentTime = dis.readInt();
        this.nextTrackId = dis.readInt();
    }

    /* */
    public String toString() {
        return String.format("mvhd: creationTime: %1$tF %1$tT, modificationTime: %2$tF %2$tT, timeScale: %3$d, duration: %4$d, preferredRate: %5$d\n",
                             qtTimeToLong(creationTime),
                             qtTimeToLong(modificationTime),
                             timeScale,
                             duration,
                             preferredRate);
    }
}

/* */
