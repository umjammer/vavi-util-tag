/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.box;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.TimeZone;

import vavi.util.StringUtil;


/**
 * Box.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 * @see "https://developer.apple.com/mac/library/documentation/QuickTime/QTFF/"
 */
public class Box {
    /** */
    protected long offset;

    /** */
    protected byte[] id = new byte[4];

    /** */
    protected byte[] data;

    /** */
    public void setOffset(long offset) {
        this.offset = offset;
    }

    /** */
    public void setId(byte[] id) {
        this.id = id;
    }

    /** */
    public byte[] getId() {
        return id;
    }

    /** */
    public boolean isIdOf(String idString) {
        byte[] idBytes;
        try {
            idBytes = idString.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
System.err.println(e);
            idBytes = idString.getBytes();
        }
//System.err.println("cmp: " + StringUtil.getDump(id) + ", " + StringUtil.getDump(idBytes));
        for (int i = 0; i < 4; i++) {
            if (id[i] != idBytes[i]) {
                return false;
            }
        }
        return true;
    }

    /** */
    public void setData(byte[] data) {
        this.data = data;
    }

    /** */
    public byte[] getData() {
        return data;
    }

    /**
     * Reads after offset, id.
     */
    public void inject(DataInputStream dis) throws IOException {
        int length = (int) offset - 8; // TODO 64 bit
        byte[] data = new byte[length];
        dis.readFully(data);

        this.data = data;
    }

    /* */
    public String toString() {
        return "id: " + new String(id) + ", offset: " + offset + (data != null ? "\n" + StringUtil.getDump(data, 128) : "\n");
    }

    /**
     * DATE (int) から java long (msec since 1970/1/1) を取得します．
     */
    protected static final long qtTimeToLong(int qtTime) {
//Debug.println("date: " + qtTime);
        long time = qtTime;
        if (qtTime < 0) {
            time = qtTime + 0x100000000l;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(1904, Calendar.JANUARY, 1, 00, 00, 00);
        return calendar.getTime().getTime() + time * 1000;
    }

    /** */
    protected static final int longToQtTime(long time) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.set(1904, Calendar.JANUARY, 1, 00, 00, 00);
        return (int) (((time - calendar.getTime().getTime() + 999) / 1000) & 0xffffffff);
    }

    /** TODO */
    protected BoxFactory factory;

    /** TODO */
    public void setFactory(BoxFactory factory) {
        this.factory = factory;
    }
}

/* */
