/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.StringUtil;
import vavi.util.box.Box;


/**
 * <pre>
 * /uuid
 * </pre>
 *
 * @see "http://www.nurs.or.jp/~calcium/wiki/index.php?3GPP2%20File%20Format"
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 071121 nsano initial version <br>
 */
public class uuid extends Box {

    byte[] subType = new byte[4];

    SubBox subBox;

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        dis.readFully(subType);
        String subTypeString = new String(subType);
        if ("cpgd".equals(subTypeString)) {
            subBox = new cpgd();
            subBox.inject(dis);
        } else {
            int length = (int) offset - 8 - 4;
            byte[] data = new byte[length];
            dis.readFully(data);

            this.data = data;
        }
    }

    /* */
    public String toString() {
        return "uuid: " + (subBox == null ? new String(subType) + "\n" + StringUtil.getDump(data) : subBox);
    }

    interface SubBox {
        void inject(DataInputStream dis) throws IOException;
    }

    static class cpgd implements SubBox {
        /** */
        byte[] uuid = new byte[12];

        /** */
        int type;           // times: 4, days: 2, date: 1, off: 0
        /** */
        int transportFlag;  // times: 1, days: 1, date: 1, off: 0

        /** */
        int dateLimit;
        /** */
        int daysLimit;
        /** */
        int timesLimit;

        /** */
        public void inject(DataInputStream dis) throws IOException {
            dis.readFully(this.uuid);
            this.type = dis.readInt();
            this.transportFlag = dis.readInt();
            this.dateLimit = dis.readInt();
            this.daysLimit = dis.readInt();
            this.timesLimit = dis.readInt();
        }

        /* */
        public String toString() {
            return String.format("cpgd: type: %1$d, transportFlag: %2$d, dateLimit: %3$tF %3$tT, daysLimit: %4$d, timesLimit: %5$d",
                                 type, transportFlag, qtTimeToLong(dateLimit), daysLimit, timesLimit);
        }
    }
}

/* */
