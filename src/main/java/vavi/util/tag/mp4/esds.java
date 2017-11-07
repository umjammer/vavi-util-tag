/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;

import vavi.util.StringUtil;
import vavi.util.box.FullBox;


/**
 * <pre>
 * /moov/trak/mdia/minf/stbl/stsd/mp4a/esds
 * </pre>
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070611 nsano initial version <br>
 */
public class esds extends FullBox {

    /** */
    @Override
    public void inject(DataInputStream dis) throws IOException {
        super.injectBase(dis);
//Debug.println("version: " + version);
        long rest = offset - 8 - 4;
//Debug.println("rest: " + rest);
        while (rest > 0) {
            Description desc = Description.readFrom(dis);
//Debug.println("desc: " + desc);
            rest -= desc.totalLength;
//Debug.println("rest: " + rest);
        }
    }

    static class Description {
        static final int ODescrTag = 0x01;
        static final int IODescrTag = 0x02;
        static final int ESDescrTag = 0x03;
        static final int DecConfigDescrTag = 0x04;
        static final int DecSpecificDescrTag = 0x05;
        static final int SLConfigDescrTag = 0x06;
        static final int ContentIdDescrTag = 0x07;
        static final int SupplContentIdDescrTag = 0x08;
        static final int IPIPtrDescrTag = 0x09;
        static final int IPMPPtrDescrTag = 0x0a;
        static final int IPMPDescrTag = 0x0b;
        static final int RegistrationDescrTag = 0x0d;
        static final int ESIDIncDescrTag = 0x0e;
        static final int ESIDRefDescrTag = 0x0f;
        static final int FileIODescrTag = 0x10;
        static final int FileODescrTag = 0x11;
        static final int ExtProfileLevelDescrTag = 0x13;
        static final int ExtDescrTagsStart = 0x80;
        static final int ExtDescrTagsEnd = 0xfe;

        int tag;

        int totalLength;

        byte[] data;

        static final int[] sampleRateTable = {
            96000, 88200, 64000, 48000, 44100, 32000,
            24000, 22050, 16000, 12000, 11025, 8000,
            7350, 0, 0, 0
        };

        /** */
        static Description readFrom(DataInputStream dis) throws IOException {
            Description desc = new Description();

            desc.tag = dis.readUnsignedByte();
            desc.totalLength = 1;

            int[] result = readTagLength(dis);
            desc.totalLength += result[1];
            int length = result[0];
//Debug.println("tag: " + desc.tag + ", length: " + length);

            switch (desc.tag) {
            case ESDescrTag: {
                int dummy = dis.readUnsignedShort();    // elementary stream id
//Debug.println("elementary stream id: " + dummy);
                desc.totalLength += 2;
                dummy = dis.readUnsignedByte();         // stream priority
//Debug.println("stream priority: " + dummy);
                desc.totalLength += 1;
                Description subDesc = Description.readFrom(dis);
//Debug.println("subDesc: " + subDesc);
                desc.totalLength += subDesc.totalLength;
            }
                break;
            case DecConfigDescrTag: {
                int dummy = dis.readUnsignedByte();     // object type id, 64: aac, 225: qcelp?
//Debug.println("object type id: " + dummy);
                desc.totalLength += 1;
                dummy = dis.readUnsignedByte();         // stream type
//Debug.println("stream type: " + dummy);
                desc.totalLength += 1;
                dummy = dis.readUnsignedShort();        // buffer size 16/24
                int dummy2 = dis.readUnsignedByte();    // 8/24
//Debug.println("buffer size: " + ((dummy << 8) | dummy2));
                desc.totalLength += 3;
                dummy = dis.readInt();                  // max bitrate
//Debug.println("max bitrate: " + dummy);
                desc.totalLength += 4;
                dummy = dis.readInt();                  // average bitrate
//Debug.println("average bitrate: " + dummy);
                desc.totalLength += 4;
                Description subDesc = Description.readFrom(dis);
//Debug.println("subDesc: " + subDesc);
                desc.totalLength += subDesc.totalLength;
            }
                break;
//            case DecSpecificDescrTag: {
//                switch (objectType) {
//                case 64: {
//                    desc.data = new byte[length]; // 2byte
//                    dis.readFully(desc.data);
//                    desc.totalLength += length;
//                    int sampleRate = ((desc.data[0] & 7) << 1) + ((desc.data[1] >> 7) & 1);
//                    int channels = (desc.data[1] >> 3) & 15;
//                }
//                    break;
//                }
//            }
//                break;
            default: {
                desc.data = new byte[length];
                dis.readFully(desc.data);
                desc.totalLength += length;
            }
                break;
            }

            return desc;
        }

        /** TODO too bad */
        static int[] readTagLength(DataInputStream dis) throws IOException {
            int length = 0;
            int count = 4;
            while (count-- > 0) {
                int c = dis.readUnsignedByte();
                length = (length << 7) | (c & 0x7f);
                if ((c & 0x80) == 0) {
                    break;
                }
            }
            return new int[] { length, 4 - count };
        }

        /* */
        public String toString() {
            return "tag: " + tag + ", totalLength: " + totalLength + "\n" + (data != null ? StringUtil.getDump(data, 128) : "");
        }
    }

    /* */
    public String toString() {
        return StringUtil.paramString(this) + "\n"; // TODO
    }
}

/* */
