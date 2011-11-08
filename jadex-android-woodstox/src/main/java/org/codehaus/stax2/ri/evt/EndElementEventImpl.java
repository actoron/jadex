package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamReader;
import javaxx.xml.stream.events.EndElement;
import javaxx.xml.stream.events.Namespace;

import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.evt.BaseEventImpl;

public class EndElementEventImpl
    extends BaseEventImpl
    implements EndElement
{
    final QName mName;
    final ArrayList mNamespaces;

    /**
     * Constructor usually used when reading events from a stream reader.
     */
    public EndElementEventImpl(Location loc, XMLStreamReader r)
    {
        super(loc);
        mName = r.getName();

        // Let's figure out if there are any namespace declarations...
        int nsCount = r.getNamespaceCount();
        if (nsCount == 0) {
            mNamespaces = null;
        } else {
            ArrayList l = new ArrayList(nsCount);
            for (int i = 0; i < nsCount; ++i) {
                l.add(NamespaceEventImpl.constructNamespace
                      (loc, r.getNamespacePrefix(i), r.getNamespaceURI(i)));
            }
            mNamespaces = l;
        }
    }

    /**
     * Constructor used by the event factory.
     */
    public EndElementEventImpl(Location loc, QName name, Iterator namespaces)
    {
        super(loc);
        mName = name;
        if (namespaces == null || !namespaces.hasNext()) {
            mNamespaces = null;
        } else {
            ArrayList l = new ArrayList();
            while (namespaces.hasNext()) {
                /* Let's do typecast here, to catch any cast errors early;
                 * not strictly required, but helps in preventing later
                 * problems
                 */
                l.add((Namespace) namespaces.next());
            }
            mNamespaces = l;
        }
    }

    /*
    /////////////////////////////////////////////
    // Public API
    /////////////////////////////////////////////
     */

    public QName getName() {
        return mName;
    }

    public Iterator getNamespaces() 
    {
        return (mNamespaces == null) ? EmptyIterator.getInstance()
            : mNamespaces.iterator();
    }

    /*
    /////////////////////////////////////////////////////
    // Implementation of abstract base methods, overrides
    /////////////////////////////////////////////////////
     */

    public EndElement asEndElement() { // overriden to save a cast
        return this;
    }

    public int getEventType() {
        return END_ELEMENT;
    }

    public boolean isEndElement() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        try {
            w.write("</");
            String prefix = mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(':');
            }
            w.write(mName.getLocalPart());
            w.write('>');
        } catch (IOException ie) {
            throwFromIOE(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException
    {
        w.writeEndElement();
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

        if (!(o instanceof EndElement)) return false;

        EndElement other = (EndElement) o;
        // First of all, names must match obviously
        if (getName().equals(other.getName())) {
            /* But then, what about namespaces etc? For now,
             * let's actually not consider namespaces: chances
             * are corresponding START_ELEMENT must have matched
             * well enough.
             */
            return true;
        }
        return false;
    }

    public int hashCode()
    {
        return getName().hashCode();
    }
}
