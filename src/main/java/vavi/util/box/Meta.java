/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.box;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Meta. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 070608 nsano initial version <br>
 */
public interface Meta {

    List<Box> getSubBoxes();

    class Support {
        /** */
        private List<Box> subBoxes = new ArrayList<>();
    
        /** */
        public List<Box> getSubBoxes() {
            return subBoxes;
        }
    
        /**
         * @return nullable 
         */
        public Box getSubBox(String id) {
            for (Box box : subBoxes) {
                if (box.isIdOf(id)) {
                    return box;
                }
            }
            return null;
        }

        /** */
        public void addSubBox(Box box) {
            subBoxes.add(box);
        }

        /** */
        public void inject(DataInputStream dis, long length, BoxFactory factory) throws IOException {
            int l = 0;
            while (l < length) {
                Box subBox = factory.getInstance(dis);
//System.err.println(subBox);
                subBoxes.add(subBox);
                l += subBox.offset;
//Debug.println(l + "/" + (offset - 8));
            }
        }
    
        /** */
        public String toString(String id) {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (Box subBox : subBoxes) {
                sb.append(id + " entry[" + i++ + "]: ");
                sb.append('\n');
                sb.append(subBox);
            }
            return sb.toString();
        }
    }
}

/* */
