/*
 * Copyright (c) 2012 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.mp4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import vavi.util.StringUtil;
import vavi.util.box.Box;
import vavi.util.box.Meta;
import vavi.util.tag.Tag;
import vavi.util.tag.TagException;


/**
 * MP4Tag. 
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2012/06/06 umjammer initial version <br>
 */
public class MP4Tag implements Tag {
    
    /** */
    private List<Box> boxes;

    /** */
    public MP4Tag(List<Box> boxes) {
        this.boxes = boxes;
    }

    List<Object> search(List<Box> boxes, String key, List<Object> results, int depth) {
        for (Box box : boxes) {
System.err.print("BOX" + depth + ": " + StringUtil.getDump(box.getId()));
            if (box.isIdOf(key)) {
                results.add(box);
            } else {
                if (Meta.class.isInstance(box)) {
                    search(Meta.class.cast(box).getSubBoxes(), key, results, depth + 1);
                }
            }
        }
        return results;
    }

    public Object getTag(String key) throws TagException {
        List<Object> results = new ArrayList<Object>(); 
        return search(this.boxes, key, results, 0);
    }

    public void setTag(String key, Object value) throws TagException {
        // TODO Auto-generated method stub

    }

    public void update() throws IOException {
        // TODO Auto-generated method stub

    }

    public Iterator<?> tags() throws TagException {
        List<Object> results = new ArrayList<Object>(); 
        return search(this.boxes, results).iterator();
    }

    List<Object> search(List<Box> boxes, List<Object> results) {
        for (Box box : boxes) {
            results.add(box);
            if (Meta.class.isInstance(box)) {
                search(Meta.class.cast(box).getSubBoxes(), results);
            }
        }
        return results;
    }
}

/* */
