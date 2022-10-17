package vavi.util.tag.mp4;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.MetaBox;
import vavi.util.box.MetaFullBox;


/**
 * 
 * <pre>
 *   alb    Album
 *  apid    Apple Store ID
 *   ART    Artist
 *   cmt    Comment
 *  covr    Album art (typically jpeg data)
 *  cpil    Compilation (boolean)
 *  cprt    Copyright statement
 *   day    Year
 *  disk    Disk number &amp; total (2 integers)
 *  gnre    Genre
 *   grp    Grouping
 *   nam    Title
 *  rtng    Rating (integer)
 *  tmpo    Tempo (integer)
 *   too    Encoder
 *  trkn    Track number &amp; total (2 integers)
 *   wrt    Author or composer
 * </pre>
 */
public class MP4BoxFactory implements BoxFactory {
    /** */
    public Box getInstance(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        long offset = dis.readInt();
        byte[] id = new byte[4];
        dis.readFully(id);
        if (offset == 1) {
            offset = dis.readLong();
Debug.println("64 bit length: " + offset);
        }

        Box box = null;
        String idString = new String(id, StandardCharsets.ISO_8859_1);
        if ("ftyp".equals(idString)) {          // L1
            box = new ftyp();
        } else if ("moov".equals(idString)) {   // L1
            box = new MetaBox();
        } else if ("mvhd".equals(idString)) {   // L2
            box = new mvhd();
        } else if ("trak".equals(idString)) {   // L2
            box = new MetaBox();
        } else if ("mdia".equals(idString)) {   // L3
            box = new MetaBox();
        } else if ("tkhd".equals(idString)) {   // L3
            box = new tkhd();
        } else if ("mdhd".equals(idString)) {   // L4
            box = new mdhd();
        } else if ("minf".equals(idString)) {   // L4
            box = new MetaBox();
        } else if ("stbl".equals(idString)) {   // L5
            box = new MetaBox();
        } else if ("udta".equals(idString)) {   // L2
            box = new MetaBox();
        } else if ("dinf".equals(idString)) {   // L5
            box = new MetaBox();
        } else if ("hdlr".equals(idString)) {   // L4
            box = new hdlr();
        } else if ("stsd".equals(idString)) {   // L6
            box = new stsd();
        } else if ("mp4a".equals(idString)) {
            box = new mp4a();
        } else if ("esds".equals(idString)) {
            box = new esds();
        } else if ("wave".equals(idString)) {
            box = new MetaBox();
        } else if ("meta".equals(idString)) {
            box = new MetaFullBox();
        } else if ("ilst".equals(idString)) {
            box = new MetaBox();
        } else if ("uuid".equals(idString)) {   // au 3gpp2
            box = new uuid();
        } else if ("----".equals(idString)) {   // iTunes
            box = new ____();
        } else if ("covr".equals(idString)) {   // iTunes
            box = new covr();
        } else if ("pinf".equals(idString)) {   // iTunes
            box = new MetaBox();
        } else if ("schi".equals(idString)) {   // iTunes
            box = new MetaBox();
        } else if (((char) 0xa9 + "nam").equals(idString)) { // iTunes
            box = new _nam();
        } else if (((char) 0xa9 + "ART").equals(idString)) { // iTunes
            box = new _ART();
        } else {
            box = new Box();
        }
        box.setFactory(this); // TODO bad!
        box.setOffset(offset);
        box.setId(id);
//Debug.println("id: " + new String(id) + ", length: " + StringUtil.toHex16(offset));
        box.inject(dis);
        return box;
    }
}

/* */
