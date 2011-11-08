package org.codehaus.stax2.ri.evt;

import javaxx.xml.XMLConstants;
import javaxx.xml.stream.*;
import javaxx.xml.stream.events.Namespace;

/**
 * Implementation of {@link Namespace}. Only returned via accessors in
 * actual "first class" event objects (start element, end element); never
 * directly via event reader.
 */
public class NamespaceEventImpl
    extends AttributeEventImpl
    implements Namespace
{
    final String mPrefix;
    final String mURI;

    /**
     * Constructor for default namespace declaration. Such declarations don't
     * have namespace prefix/URI, although semantically it would belong
     * to XML namespace URI...
     */
    protected NamespaceEventImpl(Location loc, String nsURI)
    {
        super(loc, XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
              null,
              nsURI, true);
        mPrefix = "";
        mURI = nsURI;
    }

    /**
     * Constructor for non-default namespace declaration. Such declarations
     * belong to "XML namespace" namespace.
     */
    protected NamespaceEventImpl(Location loc, String nsPrefix, String nsURI)
    {
        super(loc, nsPrefix,  XMLConstants.XMLNS_ATTRIBUTE_NS_URI,
              XMLConstants.XMLNS_ATTRIBUTE,
              nsURI, true);
        mPrefix = nsPrefix;
        mURI = nsURI;
    }

    public static NamespaceEventImpl constructDefaultNamespace(Location loc, String nsURI)
    {
        return new NamespaceEventImpl(loc, nsURI);
    }

    public static NamespaceEventImpl constructNamespace(Location loc, String nsPrefix, String nsURI)
    {
        if (nsPrefix == null || nsPrefix.length() == 0) { // default NS:
            return new NamespaceEventImpl(loc, nsURI);
        }
        return new NamespaceEventImpl(loc, nsPrefix, nsURI);
    }

    public String getNamespaceURI() {
        return mURI;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public boolean isDefaultNamespaceDeclaration() {
        return (mPrefix.length() == 0);
    }

    /*
    ///////////////////////////////////////////
    // Implementation of abstract base methods
    ///////////////////////////////////////////
     */

    public int getEventType() {
        return NAMESPACE;
    }

    public boolean isNamespace() {
        return true;
    }

    // Attribute's implementation for these should be ok:

    //public void writeAsEncodedUnicode(Writer w) throws XMLStreamException
    //public void writeUsing(XMLStreamWriter2 w) throws XMLStreamException

    //public boolean equals(Object o)
    //public int hashCode()
}
