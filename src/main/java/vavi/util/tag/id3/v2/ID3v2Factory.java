/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import vavi.util.tag.id3.v2.impl.ID3v2FrameV220;
import vavi.util.tag.id3.v2.impl.ID3v2FrameV230;
import vavi.util.tag.id3.v2.impl.ID3v2FrameV240;
import vavi.util.tag.id3.v2.impl.ID3v2HeaderV220;
import vavi.util.tag.id3.v2.impl.ID3v2HeaderV230;
import vavi.util.tag.id3.v2.impl.ID3v2HeaderV240;


/**
 * ID3v2Factory. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051207 nsano initial version <br>
 */
@SuppressWarnings("unchecked")
public class ID3v2Factory {

    private static Logger logger = Logger.getLogger(ID3v2Factory.class.getName());

    /** */
    private ID3v2Factory() {
    }

    /** */
    public static ID3v2Header readHeaderFrom(InputStream in) throws ID3v2Exception, IOException {
        byte[] head = new byte[10];
        int r = in.read(head);
        if (r < 0) {
            throw new EOFException();
        }

        int version = head[3];
        int revision = head[4];
        switch (version) {
        case 2:
            return new ID3v2HeaderV220(head);
        case 3:
            switch (revision) {
            case 1: 
                return new ID3v2HeaderV240(head);
            default:
                return new ID3v2HeaderV230(head);
            }
        case 4:
            return new ID3v2HeaderV240(head);
        default:
            throw new ID3v2Exception("illegal version: " + version + "." + revision);
        }
    }

    /** */
    public static ID3v2Frame readFrameFrom(InputStream in, ID3v2Header header) throws ID3v2Exception, IOException {
        switch (header.getVersion()) {
        case 2:
            return new ID3v2FrameV220(in, header);
        case 3:
            switch (header.getRevision()) {
            case 1: 
                return new ID3v2FrameV240(in);
            default:
                return new ID3v2FrameV230(in);
            }
        case 4:
            return new ID3v2FrameV240(in);
        default:
            throw new ID3v2Exception("illegal version: " + header.getVersion() + "." + header.getRevision());
        }
    }

    /** */
    public static ID3v2Frame createFrame(String key, FrameContent content) throws ID3v2Exception {
        return null;
    }

    /** read from file */
    public static FrameContent createFrameContent(String key, byte[] content) {
        try {
            Constructor<FrameContent> constructor = constructorsWithArgs.get(key);
            FrameContent frameContent = constructor.newInstance(content);
            return frameContent;
        } catch (Exception e) {
if (key.matches("[A-Z0-9]{4}")) {
 return new vavi.util.tag.id3.v2.di.RawFrameContent(content);
} else {
 logger.warning(key);
 e.printStackTrace();
            throw new IllegalStateException(e);
}
        }
    }

    /** from java */
    public static FrameContent createFrameContent(String key, Object content) {
        try {
            Constructor<FrameContent> constructor = constructors.get(key);
            FrameContent frameContent = constructor.newInstance();
            frameContent.setContent(content);
            return frameContent;
        } catch (Exception e) {
logger.warning(key);
            throw new IllegalStateException(e);
        }
    }

    /** */
    private static final Map<String, Constructor<FrameContent>> constructorsWithArgs = new HashMap<String, Constructor<FrameContent>>();

    /** */
    private static final Map<String, Constructor<FrameContent>> constructors = new HashMap<String, Constructor<FrameContent>>();

    /** */
    static {
        try {
            Properties props = new Properties();
            props.load(ID3v2Factory.class.getResourceAsStream("/vavi/util/tag/id3/v2/di.properties"));
            Enumeration<?> e = props.propertyNames();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String className = props.getProperty(key);
                Class<FrameContent> clazz = (Class<FrameContent>) Class.forName(className);
                Constructor<FrameContent> constructor = clazz.getConstructor(byte[].class);
                constructorsWithArgs.put(key, constructor);
                constructor = clazz.getConstructor();
                constructors.put(key, constructor);
            }
        } catch (Exception e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }
}

/* */
