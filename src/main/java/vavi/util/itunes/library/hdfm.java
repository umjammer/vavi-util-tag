/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.itunes.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import vavi.util.Debug;
import vavi.util.box.Box;
import vavi.util.box.BoxFactory;
import vavi.util.box.BoxFactory.BoxFactoryFactory;


/**
 * hdfm.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 070904 nsano initial version <br>
 */
public class hdfm extends Box {

    int fileLength;
    int data1;
    int lengthOfVersionString;
    String versionString;

    public String toString() {
        String sb = '\n' +
                "fileLength: " +
                fileLength +
                '\n' +
                "data1: " +
                data1 +
                '\n' +
                "lengthOfVersionString: " +
                lengthOfVersionString +
                '\n' +
                "versionString: " +
                versionString +
                '\n';
        return sb;
    }

    /** */
    public void inject(DataInputStream dis) throws IOException {
        this.fileLength = dis.readInt();
        this.data1 = dis.readInt();
        this.lengthOfVersionString = dis.readUnsignedByte();
        byte[] versionBytes = new byte[this.lengthOfVersionString];
        dis.readFully(versionBytes);
        this.versionString = new String(versionBytes, StandardCharsets.US_ASCII);
        dis.skipBytes((int) this.offset - 17 - this.lengthOfVersionString);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while (true) {
            int r = dis.read(buf);
            if (r < 0) {
                break;
            }
            baos.write(buf, 0, r);
        }
        byte[] decrypted = crypt(this.versionString, baos.toByteArray(), Cipher.DECRYPT_MODE);
        byte[] inflated = inflate(decrypted);

        this.data = !Arrays.equals(decrypted, inflated) ? inflated : decrypted;
//Debug.println(this + "\n" + StringUtil.getDump(this.data, 128));

        //
        BoxFactory factory = BoxFactoryFactory.getFactory(ITLBoxFactory.class.getName());

        InputStream is = new ByteArrayInputStream(this.data);
        while (is.available() > 0) {
            Box box = factory.getInstance(is);
Debug.println(box);
        }
    }

    /* */
    private byte[] crypt(String version, byte[] src, int mode) throws IOException {
        byte[] dst = new byte[src.length];

        try {
            byte[] rawKey = "BHUILuilfghuila3".getBytes(StandardCharsets.US_ASCII);

            SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(mode, skeySpec);

            int encryptedLength = src.length;

            if (isAtLeast(version, 10)) {
                encryptedLength = Math.min(encryptedLength, 102400);
            }

            encryptedLength -= encryptedLength % 16;

            int padding = src.length - encryptedLength;

            byte[] result = cipher.doFinal(src, 0, encryptedLength);
            System.arraycopy(result, 0, dst, 0, result.length);
            System.arraycopy(src, result.length, dst, result.length, padding);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(e);
        }

        return dst;
    }

    /* */
    private boolean isAtLeast(String fullVersion, int majorVersion) {
        int endOfFirstNumber = fullVersion.indexOf('.');
        if (endOfFirstNumber < 0) {
            endOfFirstNumber = fullVersion.length();
        }

        try {
            return Integer.parseInt(fullVersion.substring(0, endOfFirstNumber)) >= majorVersion;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /* */
    private byte[] inflate(byte[] src) throws IOException {

        // Check for a zlib flag byte; 0x78 => 32k window, deflate
        boolean probablyCompressed = src.length >= 1 && src[0] == 0x78;

        try {
            InflaterInputStream in = new InflaterInputStream(new ByteArrayInputStream(src), new Inflater());
            ByteArrayOutputStream out = new ByteArrayOutputStream(src.length);
            byte[] dst = new byte[src.length];
            while (true) {
                int r = in.read(dst, 0, src.length);
                if (r == -1) {
                    break;
                }
                out.write(dst, 0, r);
            }
            dst = out.toByteArray();
            out.close();
            in.close();

            return dst;

        } catch (ZipException e) {

            if (probablyCompressed) {
                throw e;
            }
            // If a ZipException occurs, it's probably because "orig" isn't actually compressed data,
            // because it's from an earlier version of iTunes.
            // So since there's nothing to decompress, just return the array that was passed in, unchanged.
System.err.println(e);
            return src;
        }
    }
}

/* */
