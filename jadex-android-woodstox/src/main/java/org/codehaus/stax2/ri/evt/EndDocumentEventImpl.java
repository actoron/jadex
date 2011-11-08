package org.codehaus.stax2.ri.evt;

import java.io.Writer;

import javaxx.xml.stream.*;
import javaxx.xml.stream.events.EndDocument;

import org.codehaus.stax2.XMLStreamWriter2;

public class EndDocumentEventImpl
    extends BaseEventImpl
    implements EndDocument
{
    public EndDocumentEventImpl(Location loc)
    {
        super(loc);
    }

    /*
    ///////////////////////////////////////////
    // Implementation of abstract base methods
    ///////////////////////////////////////////
     */

    public int getEventType() {
        return END_DOCUMENT;
    }

    public boolean isEndDocument() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        // Nothing to output
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException
    {
        w.writeEndDocument();
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
        return (o instanceof EndDocument);
    }

    public int hashCode()
    {
        return END_DOCUMENT;
    }
}
