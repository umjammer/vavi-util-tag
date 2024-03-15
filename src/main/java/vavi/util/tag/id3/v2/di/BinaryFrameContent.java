// BinaryFrame
// $Id: BinaryFrame.java,v 1.1 2003/07/05 18:43:36 axelwernicke Exp $
//
// de.vdheide.mp3: Access MP3 properties, ID3 and ID3v2 tags
// Copyright (C) 1999 Jens Vonderheide <jens@vdheide.de>
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Library General Public
// License as published by the Free Software Foundation; either
// version 2 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Library General Public License for more details.
//
// You should have received a copy of the GNU Library General Public
// License along with this library; if not, write to the
// Free Software Foundation, Inc., 59 Temple Place - Suite 330,
// Boston, MA  02111-1307, USA.

package vavi.util.tag.id3.v2.di;

import vavi.util.StringUtil;
import vavi.util.tag.id3.CharConverter;
import vavi.util.tag.id3.v2.FrameContent;
import vavi.util.tag.id3.v2.ID3v2Exception;


/**
 * BinaryFrameContent.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 051227 nsano initial version <br>
 */
public class BinaryFrameContent extends FrameContent {
    /** jdk1.4 logger */
//  private static Logger logger = Logger.getLogger(BinaryFrameContent.class.getName());

    /** */
    public BinaryFrameContent() {
    }

    /** */
    public BinaryFrameContent(byte[] content) {
        this.content = content;
    }

    /** */
    public byte[] toByteArray() throws ID3v2Exception {
        // check correct format
        if (content == null) {
            throw new ID3v2Exception("tag format");
        }

        ByteBuilder build = new ByteBuilder(ByteBuilder.NONE, ((byte[]) content).length);

        build.put((byte[]) content);

        return build.getBytes();
    }

    /** */
    public String toString() {
//        return "binary: " + (((byte[]) content).length) + " bytes";
        if (content == null) {
            return null;
        } else {
            if (((byte[]) content).length < 3 || ((byte[]) content).length > 1024) {
                return "binary: " + (((byte[]) content).length) + " bytes" + "\n" + StringUtil.getDump((byte[]) content, 64);
            } else {
                return "binary: " + (((byte[]) content).length) + " bytes" + "\n" + StringUtil.getDump((byte[]) content, 64) + CharConverter.createString2((byte[]) content, 0, ((byte[]) content).length);
            }
        }
    }
}
