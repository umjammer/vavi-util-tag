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
 * item. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class item extends Box {

    /** */
    int dataHeaderLength;
    byte[] disposableInformation; // 16 | 20
    byte[] libraryPersistentID = new byte[8];
    byte[] trackPersistentID = new byte[8];
    // 64 6F 77 6E (down) or 6C 6F 63 6C (locl)
    byte[] downloadPersistenceIndicator = new byte[4];
    // 50 4E 47 66 (PNGf), 00 00 00 0D JPEG 
    byte[] pseudoFileFormat = new byte[4];
    byte[] disposableInformation2 = new byte[4];
    int imageWidth;
    int imageHeight;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        this.dataHeaderLength = dis.readInt();
        this.disposableInformation = new byte[dataHeaderLength - 196];
        dis.readFully(this.disposableInformation);
        dis.readFully(this.libraryPersistentID);
        dis.readFully(this.trackPersistentID);
        dis.readFully(this.downloadPersistenceIndicator);
        dis.readFully(this.pseudoFileFormat);
        dis.readFully(this.disposableInformation2);
        this.imageWidth = dis.readInt();
        this.imageHeight = dis.readInt();
        int l = dataHeaderLength - 8 - 40 - disposableInformation.length;
        dis.skip(l);
        l = (int) offset - dataHeaderLength;
//System.err.println("available: " + dis.available() + ", " + l);
        this.data = new byte[l];
        dis.readFully(data);
//System.err.println(super.toString());
    }

    /* */
    public String toString() {
        return String.format("item: downloadPersistenceIndicator: %s, pseudoFileFormat: %s, imageWidth: %d, imageHeight: %d, dataHeaderLength: %d\n",
                             new String(downloadPersistenceIndicator),
                             new String(pseudoFileFormat),
                             imageWidth,
                             imageHeight,
                             dataHeaderLength);
    }
}

/* */
