/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import java.lang.reflect.Constructor;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * FrameText. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
@SuppressWarnings("unchecked")
public interface FrameText {
    /** */
    String getText(byte[] content, int start, String encoding);

    /** */
    class Util {
        /** */
        protected static int getLastZeros(byte[] content, int max) {
            int c = 0;
            for (int i = 0; i < content.length; i++) {
                if (content[content.length - 1 - i] == 0) {
                    if (c == max) {
                        break;
                    } else {
                        c++;
                    }
                } else {
                    break;
                }
            }
            return c;
        }
    }

    /** */
    class Factory {

        /**
         * @param description
         * @param defaultKey
         * @return FrameText
         */
        public static FrameText getFrameText(String description, String defaultKey) {
            try {
                Constructor<FrameText> constructor = null;
                if (constructors.containsKey(description)) {
                    constructor = constructors.get(description);
                } else {
                    constructor = constructors.get(defaultKey);
                }
                return constructor.newInstance();
            } catch (Exception e) {
                throw (RuntimeException) new IllegalStateException().initCause(e);
            }
        }

        /** */
        private static final Map<String, Constructor<FrameText>> constructors = new HashMap<String, Constructor<FrameText>>();
    
        /** */
        static {
            try {
                Properties props = new Properties();
                props.load(EncodedTextWithLanguageAndDescriptionFrameContent.class.getResourceAsStream("/vavi/util/tag/id3/v2/di/description.properties"));
                Enumeration e = props.propertyNames();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    String className = props.getProperty(key);
                    Class clazz = Class.forName(className);
                    Constructor constructor = clazz.getConstructor();
                    constructor = clazz.getConstructor();
                    constructors.put(key, constructor);
                }
            } catch (Exception e) {
                throw (RuntimeException) new IllegalStateException().initCause(e);
            }
        }
    }
}

/* */
