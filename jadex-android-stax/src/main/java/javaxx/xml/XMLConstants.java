// $Id$

package javaxx.xml;

/**
 * Utility class to contain basic XML values as constants.
 *
 * @author <a href="http://jcp.org/">JAXP Java Community Process</a>
 * @author <a href="http://java.sun.com/">JAXP Reference Implementation</a>
 * @version 1.0.proposed
 * @see <a href="http://www.w3.org/TR/REC-xml">
 *        Extensible Markup Language (XML) 1.0 (Second Edition)</a>
 * @see <a href="http://www.w3.org/TR/REC-xml-names">
 *        Namespaces in XML</a>
 * @see <a href="http://www.w3.org/XML/xml-names-19990114-errata">
 *        Namespaces in XML Errata</a>
 **/

public class XMLConstants {

    /**
     * Constructor to prevent instantiation.
     */
    private XMLConstants() { }
    
    /**
     * Added
     */
    public static final String NULL_NS_URI = "";

    /**
     * Prefix to use to represent the default XML Namespace.
     *
     * <p>Defined by the XML specification to be "".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     *        Namespaces in XML</a>
     */
    public static final String DEFAULT_NS_PREFIX = "";

    /**
     * The official XML Namespace prefix.
     *
     * <p>Defined by the XML specification to be "<code>xml</code>".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     *        Namespaces in XML</a>
     */
    public static final String XML_NS_PREFIX = "xml";

    /**
     * The official XML Namespace name URI.
     *
     * <p>Defined by the XML specification to be
     * "<code>http://www.w3.org/XML/1998/namespace</code>".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     *        Namespaces in XML</a>
     */
    public static final String XML_NS_URI =
        "http://www.w3.org/XML/1998/namespace";

    /**
     * The official XML attribute used for specifying XML Namespace
     * declarations.
     *
     * <p>It is <strong>not</strong> valid to use as a prefix.
     * Defined by the XML specification to be
     * "<code>xmlns</code>".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     *        Namespaces in XML</a>
     */
    public static final String XMLNS_ATTRIBUTE = "xmlns";

    /**
     * The official XML attribute used for specifying XML Namespace
     * declarations, {@link #XMLNS_ATTRIBUTE "xmlns"}, Namespace name
     * URI.
     *
     * <p>Defined by the XML specification to be
     * "<code>http://www.w3.org/2000/xmlns/</code>".</p>
     *
     * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
     *        Namespaces in XML</a>
     * @see <a href="http://www.w3.org/XML/xml-names-19990114-errata/">
     *        Namespaces in XML Errata</a>
     */
    public static final String XMLNS_ATTRIBUTE_NS_URI =
        "http://www.w3.org/2000/xmlns/";
}
