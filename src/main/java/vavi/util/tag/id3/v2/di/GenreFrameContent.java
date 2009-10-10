/*
 * Copyright (c) 2005 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.tag.id3.v2.di;

import vavi.util.tag.id3.GenreUtil;


/**
 * GenreFrameContent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class GenreFrameContent extends EncodedTextFrameContent {

    /** */
//  private static Logger logger = Logger.getLogger(GenreFrameContent.class.getName());

    /** @see #setContent(Object) */
    public GenreFrameContent() {
    }

    /**
     * Creates a new TextFrameEncoding with a given content
     */
    public GenreFrameContent(byte[] content) {
        super(content);

        String genre = (String) this.content;
        genre = genre.trim();
        if (genre.startsWith("(") && genre.endsWith(")")) {
            int genreCode = Integer.parseInt(genre.substring(1, genre.length() - 1));
            // The following genres are defined in ID3v1 
            genre = GenreUtil.getGenreString(genreCode);
            if (!"".equals(genre)) {
                this.content = genre;
            }
        }
    }

    /**
     * TODO implement!
     */
    public void setContent(Object content) {
        if (content instanceof String) {
            // TODO ‚»‚Ì‚Ü‚Ü
        } if (content instanceof Integer) {
            // TODO ID3v1 Œ`Ž®
        } else {
            throw new IllegalArgumentException("unhandled class: " + content.getClass());
        }
    }
}

/* */
