package com.ctc.wstx.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.namespace.QName;
import javaxx.xml.stream.Location;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.stream.events.Attribute;
import javaxx.xml.stream.events.Namespace;
import javaxx.xml.stream.events.StartElement;

import org.codehaus.stax2.ri.EmptyIterator;

import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.util.BaseNsContext;

/**
 * Wstx {@link StartElement} implementation used when event is constructed
 * from already objectified data, for example when constructed by the event
 * factory.
 */
public class SimpleStartElement
    extends BaseStartElement
{
    final Map mAttrs;

    /*
    /////////////////////////////////////////////
    // Life cycle
    /////////////////////////////////////////////
     */

    protected SimpleStartElement(Location loc, QName name, BaseNsContext nsCtxt,
                                 Map attr)
    {
        super(loc, name, nsCtxt);
        mAttrs = attr;
    }

    /**
     * Factory method called when a start element needs to be constructed
     * from an external source (most likely, non-woodstox stream reader).
     */
    public static SimpleStartElement construct(Location loc, QName name,
                                               Map attrs, List ns,
                                               NamespaceContext nsCtxt)
    {
        BaseNsContext myCtxt = MergedNsContext.construct(nsCtxt, ns);
        return new SimpleStartElement(loc, name, myCtxt, attrs);
    }

    public static SimpleStartElement construct(Location loc, QName name,
                                               Iterator attrs, Iterator ns,
                                               NamespaceContext nsCtxt)
    {
        Map attrMap;
        if (attrs == null || !attrs.hasNext()) {
            attrMap = null;
        } else {
            attrMap = new LinkedHashMap();
            do {
                Attribute attr = (Attribute) attrs.next();
                attrMap.put(attr.getName(), attr);
            } while (attrs.hasNext());
        }

        BaseNsContext myCtxt;
        if (ns != null && ns.hasNext()) {
            ArrayList l = new ArrayList();
            do {
                l.add((Namespace) ns.next()); // cast to catch type problems early
            } while (ns.hasNext());
            myCtxt = MergedNsContext.construct(nsCtxt, l);
        } else {
            /* Doh. Need specificially 'our' namespace context, to get them
             * output properly...
             */
            if (nsCtxt == null) {
                myCtxt = null;
            } else if (nsCtxt instanceof BaseNsContext) {
                myCtxt = (BaseNsContext) nsCtxt;
            } else {
                myCtxt = MergedNsContext.construct(nsCtxt, null);
            }
        }
        return new SimpleStartElement(loc, name, myCtxt, attrMap);
    }

    /*
    /////////////////////////////////////////////
    // Public API
    /////////////////////////////////////////////
     */

    public Attribute getAttributeByName(QName name)
    {
        if (mAttrs == null) {
            return null;
        }
        return (Attribute) mAttrs.get(name);
    }

    public Iterator getAttributes()
    {
        if (mAttrs == null) {
            return EmptyIterator.getInstance();
        }
        return mAttrs.values().iterator();
    }

    protected void outputNsAndAttr(Writer w) throws IOException
    {
        // First namespace declarations, if any:
        if (mNsCtxt != null) {
            mNsCtxt.outputNamespaceDeclarations(w);
        }
        // Then attributes, if any:
        if (mAttrs != null && mAttrs.size() > 0) {
            Iterator it = mAttrs.values().iterator();
            while (it.hasNext()) {
                Attribute attr = (Attribute) it.next();
                // Let's only output explicit attribute values:
                if (!attr.isSpecified()) {
                    continue;
                }

                w.write(' ');
                QName name = attr.getName();
                String prefix = name.getPrefix();
                if (prefix != null && prefix.length() > 0) {
                    w.write(prefix);
                    w.write(':');
                }
                w.write(name.getLocalPart());
                w.write("=\"");
                String val =  attr.getValue();
                if (val != null && val.length() > 0) {
                    TextEscaper.writeEscapedAttrValue(w, val);
                }
                w.write('"');
            }
        }
    }

    protected void outputNsAndAttr(XMLStreamWriter w) throws XMLStreamException
    {
        // First namespace declarations, if any:
        if (mNsCtxt != null) {
            mNsCtxt.outputNamespaceDeclarations(w);
        }
        // Then attributes, if any:
        if (mAttrs != null && mAttrs.size() > 0) {
            Iterator it = mAttrs.values().iterator();
            while (it.hasNext()) {
                Attribute attr = (Attribute) it.next();
                // Let's only output explicit attribute values:
                if (!attr.isSpecified()) {
                    continue;
                }
                QName name = attr.getName();
                String prefix = name.getPrefix();
                String ln = name.getLocalPart();
                String nsURI = name.getNamespaceURI();
                w.writeAttribute(prefix, nsURI, ln, attr.getValue());
            }
        }
    }
}
