/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2;


/**
 * ID3v2Frame.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051205 nsano initial version <br>
 */
public interface ID3v2Frame {

    /** */
    String ID_INVALID = null;

    /** 4cc */
    String getID();

    /** @param id 4cc */
    void setID(String id);

    /** */
    boolean getCompression();

    /** */
    void setCompression(boolean compression);

    /**
     * Calculates the number of bytes necessary to store a byte representation
     * of this frame
     */
    int getLength();

    /**
     * Returns content
     * @param key 4cc
     */
    FrameContent getContent(String key);

    /**
     * Returns an array of bytes representing this frame
     */
    byte[] getBytes();

    /** */
    boolean isFrameOf(String key);
}
/* */
