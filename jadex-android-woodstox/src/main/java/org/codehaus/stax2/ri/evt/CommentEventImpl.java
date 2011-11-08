package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;

import javaxx.xml.stream.*;
import javaxx.xml.stream.events.Comment;

import org.codehaus.stax2.XMLStreamWriter2;

public class CommentEventImpl
    extends BaseEventImpl
    implements Comment
{
    final String mContent;

    public CommentEventImpl(Location loc, String content)
    {
        super(loc);
        mContent = content;
    }

    public String getText()
    {
        return mContent;
    }

    /*
    ///////////////////////////////////////////
    // Implementation of abstract base methods
    ///////////////////////////////////////////
     */

    public int getEventType() {
        return COMMENT;
    }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        try {
            w.write("<!--");
            w.write(mContent);
            w.write("-->");
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException
    {
        w.writeComment(mContent);
    }

    /*
    ///////////////////////////////////////////
    // Standard method impl
    ///////////////////////////////////////////
     */

    public boolean equals(Object o)
    {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof Comment)) return false;

        Comment other = (Comment) o;
        return mContent.equals(other.getText());
    }

    public int hashCode()
    {
        return mContent.hashCode();
    }
}
