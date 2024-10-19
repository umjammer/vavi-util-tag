/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.box.MetaFullBox;


/**
 * <pre>
 * /moov/trak/mdia/minf/stbl/stsd/mp4a. 
 * </pre>
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070611 nsano initial version <br>
 */
public class mp4a extends MetaFullBox {
    /** */
    int channelCount;
    /** */
    int sampleSize;
    /** */
    int sampleRate;

    /**
     * <pre>
 amc
00 00 00 00 00 00  00 01  00 00 00 00  00 00 00 00   ...... .. .... ....
00 02  00 10  00 00  00 00  1F 40 00 00  00 00 00 CA .. .. .. .. .@.. ...?
65 73 64 73 00 00 00 00 03 81 3B 00 02 10 04 81      esds.........チ
32 E1 15 00 10 00 00 00 1A 90 00 00 1A 90 05 81      2£.......ミ...ミ.チ
22 51 4C 43 4D 66 6D 74 20 96 00 00 00 01 00 41      "QLCMfmt ヨ.....A
6D 7F 5E 15 B1 D0 11 BA 91 00 80 5F B4 B9 7E 02      m.^.??.?ム.タ_??~.
00 51 63 65 6C 70 20 31 33 4B 00 00 00 00 00 00      .Qcelp 13K......
00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00      ................
 mov
00 00 00 00 00 00  00 01  00 01 00 00  00 00 00 00   ...... .. .... ....
00 02  00 10  FF FE  00 00  AC 44 00 00  00 00 04 00 .. .. ?? .. ?D.. ....
00 00 06 00 00 00 00 02 00 00 00 02  00 00 00 57     ............ ...W
77 61 76 65 00 00 00 0C 66 72 6D 61 6D 70 34 61      wave....frmamp4a
00 00 00 0C 6D 70 34 61 00 00 00 00 00 00 00 23      ....mp4a.......#
65 73 64 73 00 00 00 00 03 15 00 02 00 04 0D 40      esds...........@
15 00 00 00 00 00 00 00 00 00 00 00 06 01 02 00      ................
00 00 0C 73 72 63 71 00 00 00 40 00 00 00 08 00      ...srcq...@.....
 3gp
00 00 00 00 00 00  00 01  00 00 00 00  00 00 00 00   ...... .. .... ....
00 02  00 10  00 00  00 00  3E 80 00 00  00 00 00 27 .. .. .. .. >タ.. ...'
65 73 64 73 00 00 00 00 03 19 00 02 00 04 11 40      esds...........@
15 00 00 00 00 01 38 80 00 01 38 80 05 02 14 10      ......8タ..8タ....
06 01 02                                             ...
     * </pre>
     */
    @Override
    public void inject(DataInputStream dis) throws IOException {
//logger.log(Level.TRACE, "\n" + StringUtil.getDump(dis, 128));

        super.injectBase(dis);
        if (offset - 8 - 4 == 0) { // TODO is a sepc?
            return;
        }
        dis.readUnsignedShort();
        int dataReferenceIndex = dis.readUnsignedShort();

        int version = dis.readInt(); // version, revision ??? 0: amc, 3gp, 1: mov
//logger.log(Level.TRACE, "dataReferenceIndex: " + dataReferenceIndex + ", version: " + version);
        dis.readInt();
        this.channelCount = dis.readUnsignedShort();
        this.sampleSize = dis.readUnsignedShort();
        dis.readShort(); // pre_defined 0: amc, 3gp, -2: mov
        dis.readShort(); // reserved 0
        this.sampleRate = dis.readInt() >> 16; // {timescale of media} << 16;
//logger.log(Level.TRACE, "channelCount: " + channelCount + ", sampleSize: " + sampleSize + ", sampleRate: " + sampleRate);

        long rest = offset - 8 - 8 - 20; 
        if (version == 0x10000) {
            dis.skipBytes(16);
            rest -= 16;
        }

//logger.log(Level.TRACE, "rest: " + rest);
        metaSupport.inject(dis, rest, factory); // TODO factory
    }
}
