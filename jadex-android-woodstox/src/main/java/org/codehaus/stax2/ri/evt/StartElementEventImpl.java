package org.codehaus.stax2.ri.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.StartElement;

import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;

/**
 * Wstx {@link StartElement} implementation used when event is constructed
 * from already objectified data, for example when constructed by the event
 * factory.
 */
public class StartElementEventImpl
    extends BaseEventImpl
    implements StartElement
{
    // // // Basic configuration

    protected final QName mName;

    protected final ArrayList mAttrs;

    protected final ArrayList mNsDecls;

    /**
     * Enclosing namespace context
     */
    protected NamespaceContext mParentNsCtxt;

    // // // Lazily constructed components

    NamespaceContext mActualNsCtxt = null;

    /*
    /////////////////////////////////////////////
    // Life cycle
    /////////////////////////////////////////////
     */

    protected StartElementEventImpl(Location loc, QName name,
                                    ArrayList attrs, ArrayList nsDecls,
                                    NamespaceContext parentNsCtxt)
    {
        super(loc);
        mName = name;
        mAttrs = attrs;
        mNsDecls = nsDecls;
        mParentNsCtxt = (parentNsCtxt == null) ?
            EmptyNamespaceContext.getInstance() : parentNsCtxt;
    }

    public static StartElementEventImpl construct(Location loc, QName name,
                                                  Iterator attrIt, Iterator nsDeclIt,
                                                  NamespaceContext nsCtxt)
    {
        ArrayList attrs;
        if (attrIt == null || !attrIt.hasNext()) {
            attrs = null;
        } else {
            attrs = new ArrayList();
            do {
                // Cast is only done for early catching of incorrect types
                attrs.add((Attribute) attrIt.next());
            } while (attrIt.hasNext());
        }

        ArrayList nsDecls;
        if (nsDeclIt == null || !nsDeclIt.hasNext()) {
            nsDecls = null;
        } else {
            nsDecls = new ArrayList();
            do {
                nsDecls.add((Namespace) nsDeclIt.next()); // cast to catch type problems early
            } while (nsDeclIt.hasNext());
        }
        return new StartElementEventImpl(loc, name, attrs, nsDecls, nsCtxt);
    }

    /*
    /////////////////////////////////////////////////////
    // Implementation of abstract base methods, overrides
    /////////////////////////////////////////////////////
     */

    public StartElement asStartElement() { // overriden to save a cast
        return this;
    }

    public int getEventType() {
        return START_ELEMENT;
    }

    public boolean isStartElement() {
        return true;
    }

    public void writeAsEncodedUnicode(Writer w)
        throws XMLStreamException
    {
        try {
            w.write('<');
            String prefix = mName.getPrefix();
            if (prefix != null && prefix.length() > 0) {
                w.write(prefix);
                w.write(':');
            }
            w.write(mName.getLocalPart());

            // Any namespaces?
            if (mNsDecls != null) {
                for (int i = 0, len = mNsDecls.size(); i < len; ++i) {
                    w.write(' ');
                    ((Namespace) mNsDecls.get(i)).writeAsEncodedUnicode(w);
                }
            }

            // How about attrs?
            if (mAttrs != null) {
                for (int i = 0, len = mAttrs.size(); i < len; ++i) {
                    Attribute attr = (Attribute) mAttrs.get(i);
                    // No point in adding default attributes?
                    if (attr.isSpecified()) {
                        w.write(' ');
                        attr.writeAsEncodedUnicode(w);
                    }
                }
            }

            w.write('>');
        } catch (IOException ie) {
            throw new XMLStreamException(ie);
        }
    }

    public void writeUsing(XMLStreamWriter2 sw) throws XMLStreamException
    {
        QName n = mName;
        sw.writeStartElement(n.getPrefix(), n.getLocalPart(),
                            n.getNamespaceURI());

        // Any namespaces?
        if (mNsDecls != null) {
            for (int i = 0, len = mNsDecls.size(); i < len; ++i) {
                Namespace ns = (Namespace) mNsDecls.get(i);
                String prefix = ns.getPrefix();
                String uri = ns.getNamespaceURI();
                if (prefix == null || prefix.length() == 0) {
                    sw.writeDefaultNamespace(uri);
                } else {
                    sw.writeNamespace(prefix, uri);
                }
            }
        }

        // How about attrs?
        if (mAttrs != null) {
            for (int i = 0, len = mAttrs.size(); i < len; ++i) {
                Attribute attr = (Attribute) mAttrs.get(i);
                // No point in adding default attributes?
                if (attr.isSpecified()) {
                    QName name = attr.getName();
                    sw.writeAttribute(name.getPrefix(), name.getNamespaceURI(), name.getLocalPart(), attr.getValue());
                }
            }
        }
    }

    /*
    /////////////////////////////////////////////
    // Public API
    /////////////////////////////////////////////
     */

    public final QName getName() {
        return mName;
    }

    public Iterator getNamespaces() 
    {
        return (mNsDecls == null) ?
            EmptyIterator.getInstance() : mNsDecls.iterator();
    }

    public NamespaceContext getNamespaceContext()
    {
        if (mActualNsCtxt == null) {
            if (mNsDecls == null) {
                mActualNsCtxt = mParentNsCtxt;
            } else {
                mActualNsCtxt = MergedNsContext.construct(mParentNsCtxt, mNsDecls);
            }
        }
        return mActualNsCtxt;
    }

    public String getNamespaceURI(String prefix)
    {
        if (mNsDecls != null) {
            if (prefix == null) {
                prefix = "";
            }
            for (int i = 0, len = mNsDecls.size(); i < len; ++i) {
                Namespace ns = (Namespace) mNsDecls.get(i);
                String thisPrefix = ns.getPrefix();
                if (thisPrefix == null) {
                    thisPrefix = "";
                }
                if (prefix.equals(thisPrefix)) {
                    return ns.getNamespaceURI();
                }
            }
        }

        return null;
    }

    public Attribute getAttributeByName(QName nameIn)
    {
        if (mAttrs == null) {
            return null;
        }

        String ln = nameIn.getLocalPart();
        String uri = nameIn.getNamespaceURI();
        int len = mAttrs.size();

        boolean notInNs = (uri == null || uri.length() == 0);
        for (int i = 0; i < len; ++i) {
            Attribute attr = (Attribute) mAttrs.get(i);
            QName name = attr.getName();
            if (name.getLocalPart().equals(ln)) {
                String thisUri = name.getNamespaceURI();
                if (notInNs) {
                    if (thisUri == null || thisUri.length() == 0) {
                        return attr;
                    }
                } else {
                    if (uri.equals(thisUri)) {
                        return attr;
                    }
                }
            }
        }
        return null;
    }

    public Iterator getAttributes()
    {
        if (mAttrs == null) {
            return EmptyIterator.getInstance();
        }
        return mAttrs.iterator();
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

        if (!(o instanceof StartElement)) return false;

        StartElement other = (StartElement) o;

        // First things first: names must match
        if (mName.equals(other.getName())) {
            /* Rest is much trickier. I guess the easiest way is to
             * just blindly iterate through ns decls and attributes.
             * The main issue is whether ordering should matter; it will,
             * if just iterating. Would need to sort to get canonical
             * comparison.
             */
            if (iteratedEquals(getNamespaces(), other.getNamespaces())) {
                return iteratedEquals(getAttributes(), other.getAttributes());
            }
        }
        return false;
    }

    public int hashCode()
    {
        int hash = mName.hashCode();
        hash = addHash(getNamespaces(), hash);
        hash = addHash(getAttributes(), hash);
        return hash;
    }
}
