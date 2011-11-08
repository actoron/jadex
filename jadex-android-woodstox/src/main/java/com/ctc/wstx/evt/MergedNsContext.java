package com.ctc.wstx.evt;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import javaxx.xml.XMLConstants;
import javaxx.xml.namespace.NamespaceContext;
import javaxx.xml.stream.XMLStreamException;
import javaxx.xml.stream.XMLStreamWriter;
import javaxx.xml.stream.events.Namespace;

import org.codehaus.stax2.ri.EmptyIterator;

import com.ctc.wstx.util.BaseNsContext;

/**
 * Hierarchic {@link NamespaceContext} implementation used when constructing
 * event and namespace information explicitly via
 * {@link javaxx.xml.stream.XMLEventFactory},
 * not by a stream reader.
 *<p>
 * TODO:
 *<ul>
 * <li>Figure out a way to check for namespace masking; tricky but not
 *    impossible to determine
 *  </li>
 *</ul>
 */
public class MergedNsContext
    extends BaseNsContext
{
    final NamespaceContext mParentCtxt;

    /**
     * List of {@link Namespace} instances.
     */
    final List mNamespaces;

    Map mNsByPrefix = null;

    Map mNsByURI = null;

    protected MergedNsContext(NamespaceContext parentCtxt, List localNs)
    {
        mParentCtxt = parentCtxt;
        mNamespaces = (localNs == null) ? Collections.EMPTY_LIST : localNs;
    }

    public static BaseNsContext construct(NamespaceContext parentCtxt,
                                          List localNs)
    {
        return new MergedNsContext(parentCtxt, localNs);
    }

    /*
    /////////////////////////////////////////////
    // NamespaceContext API
    /////////////////////////////////////////////
     */

    public String doGetNamespaceURI(String prefix)
    {
        // Note: base class checks for 'known' problems and prefixes:
        if (mNsByPrefix == null) {
            mNsByPrefix = buildByPrefixMap();
        }
        Namespace ns = (Namespace) mNsByPrefix.get(prefix);
        if (ns == null && mParentCtxt != null) {
            return mParentCtxt.getNamespaceURI(prefix);
        }
        return (ns == null) ? null : ns.getNamespaceURI();
    }

    public String doGetPrefix(String nsURI)
    {
        // Note: base class checks for 'known' problems and prefixes:
        if (mNsByURI == null) {
            mNsByURI = buildByNsURIMap();
        }
        Namespace ns = (Namespace) mNsByURI.get(nsURI);
        if (ns == null && mParentCtxt != null) {
            return mParentCtxt.getPrefix(nsURI);
        }
        return (ns == null) ? null : ns.getPrefix();
    }

    public Iterator doGetPrefixes(String nsURI)
    {
        // Note: base class checks for 'known' problems and prefixes:
        ArrayList l = null;

        for (int i = 0, len = mNamespaces.size(); i < len; ++i) {
            Namespace ns = (Namespace) mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            if (uri.equals(nsURI)) {
                if (l == null) {
                    l = new ArrayList();
                }
                String prefix = ns.getPrefix();
                l.add((prefix == null) ? "" : prefix);
            }
        }

        if (mParentCtxt != null) {
            Iterator it = mParentCtxt.getPrefixes(nsURI);
            if (l == null) {
                return it;
            }
            while (it.hasNext()) {
                l.add(it.next());
            }
        }

        return (l == null) ? EmptyIterator.getInstance() : l.iterator();
    }

    /*
    /////////////////////////////////////////////
    // Extended API
    /////////////////////////////////////////////
     */

    /**
     * Method that returns information about namespace definition declared
     * in this scope; not including ones declared in outer scopes.
     */
    public Iterator getNamespaces()
    {
        return mNamespaces.iterator();
    }

    public void outputNamespaceDeclarations(Writer w) throws IOException
    {
        for (int i = 0, len = mNamespaces.size(); i < len; ++i) {
            Namespace ns = (Namespace) mNamespaces.get(i);
            w.write(' ');
            w.write(XMLConstants.XMLNS_ATTRIBUTE);
            if (!ns.isDefaultNamespaceDeclaration()) {
                w.write(':');
                w.write(ns.getPrefix());
            }
            w.write("=\"");
            w.write(ns.getNamespaceURI());
            w.write('"');
        }
    }

    /**
     * Method called by the matching start element class to
     * output all namespace declarations active in current namespace
     * scope, if any.
     */
    public void outputNamespaceDeclarations(XMLStreamWriter w) throws XMLStreamException
    {
        for (int i = 0, len = mNamespaces.size(); i < len; ++i) {
            Namespace ns = (Namespace) mNamespaces.get(i);
            if (ns.isDefaultNamespaceDeclaration()) {
                w.writeDefaultNamespace(ns.getNamespaceURI());
            } else {
                w.writeNamespace(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }

    /*
    /////////////////////////////////////////////
    // Private methods:
    /////////////////////////////////////////////
     */

    private Map buildByPrefixMap()
    {
        int len = mNamespaces.size();
        if (len == 0) {
            return Collections.EMPTY_MAP;
        }

        LinkedHashMap m = new LinkedHashMap(1 + len + (len>>1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace) mNamespaces.get(i);
            String prefix = ns.getPrefix();
            if (prefix == null) { // shouldn't happen but...
                prefix = "";
            }
            m.put(prefix, ns);
        }
        return m;
    }

    private Map buildByNsURIMap()
    {
        int len = mNamespaces.size();
        if (len == 0) {
            return Collections.EMPTY_MAP;
        }

        LinkedHashMap m = new LinkedHashMap(1 + len + (len>>1));
        for (int i = 0; i < len; ++i) {
            Namespace ns = (Namespace) mNamespaces.get(i);
            String uri = ns.getNamespaceURI();
            if (uri == null) { // shouldn't happen but...
                uri = "";
            }
            m.put(uri, ns);
        }
        return m;
    }
}
