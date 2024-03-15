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
        box = switch (idString) {
            case "ftyp" ->           // L1
                    new ftyp();
            case "moov" ->    // L1
                    new MetaBox();
            case "mvhd" ->    // L2
                    new mvhd();
            case "trak" ->    // L2
                    new MetaBox();
            case "mdia" ->    // L3
                    new MetaBox();
            case "tkhd" ->    // L3
                    new tkhd();
            case "mdhd" ->    // L4
                    new mdhd();
            case "minf" ->    // L4
                    new MetaBox();
            case "stbl" ->    // L5
                    new MetaBox();
            case "udta" ->    // L2
                    new MetaBox();
            case "dinf" ->    // L5
                    new MetaBox();
            case "hdlr" ->    // L4
                    new hdlr();
            case "stsd" ->    // L6
                    new stsd();
            case "mp4a" -> new mp4a();
            case "esds" -> new esds();
            case "wave" -> new MetaBox();
            case "meta" -> new MetaFullBox();
            case "ilst" -> new MetaBox();
            case "uuid" ->    // au 3gpp2
                    new uuid();
            case "----" ->    // iTunes
                    new ____();
            case "covr" ->    // iTunes
                    new covr();
            case "pinf" ->    // iTunes
                    new MetaBox();
            case "schi" ->    // iTunes
                    new MetaBox();
            case ((char) 0xa9 + "nam") ->  // iTunes
                    new _nam();
            case ((char) 0xa9 + "ART") ->  // iTunes
                    new _ART();
            default -> new Box();
        };
        box.setFactory(this); // TODO bad!
        box.setOffset(offset);
        box.setId(id);
//Debug.println("id: " + new String(id) + ", length: " + StringUtil.toHex16(offset));
        box.inject(dis);
        return box;
    }
}
