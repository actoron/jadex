// $Id$

package javaxx.xml.namespace;

import java.util.Iterator;

/**
 * Interface for read only XML Namespace context processing.
 *
 * <p>An XML Namespace has the properties:</p>
 * <ul>
 *   <li>Namespace URI:
 *       Namespace name expressed as a URI to which the prefix is bound</li>
 *   <li>prefix: syntactically, this is the part of the attribute name
 *       following the <code>XMLConstants.XMLNS_ATTRIBUTE</code>
 *       ("xmlns") in the Namespace declaration</li>
 *   <li>example:
 *     <code>&lt;element xmlns:prefix="http://Namespace-name-URI"&gt;</code>
 *   </li>
 * </ul>
 *
 * <p>All <code>get*(*)</code> methods operate in the current scope
 * for Namespace URI and prefix resolution.</p>
 *
 * <p>Note that a Namespace URI can be bound to
 * <strong>multiple</strong> prefixes in the current scope.  This can
 * occur when multiple <code>XMLConstants.XMLNS_ATTRIBUTE</code>
 * ("xmlns") Namespace declarations occur in the same Start-Tag and
 * refer to the same Namespace URI. e.g.<br />
 * <code><pre>
 * &lt;element xmlns:prefix1="http://Namespace-name-URI"
 *          xmlns:prefix2="http://Namespace-name-URI"&gt;
 * </pre></code><br />
 * This can also occur when the same Namespace URI is used in multiple
 * <code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns") Namespace
 * declarations in the logical parent element hierarchy.  e.g.<br />
 * <code><pre>
 * &lt;parent xmlns:prefix1="http://Namespace-name-URI">
 *   &lt;child xmlns:prefix2="http://Namespace-name-URI"&gt;
 *     ...
 *   &lt;/child&gt;
 * &lt;/parent&gt;
 * </pre></code></p>
 *
 * <p>A prefix can only be bound to a <strong>single</strong>
 * Namespace URI in the current scope.</p>
 *
 * @author <a href="http://jcp.org/">JAXP Java Community Process</a>
 * @author <a href="http://java.sun.com/">JAXP Reference Implementation</a>
 * @version 1.0.proposed
 * @see javaxx.xml.XMLConstants declarations of common XML values
 * @see <a href="http://www.w3.org/TR/REC-xml-names/#ns-qualnames">
 *        Namespaces in XML</a>
 * @see <a href="http://www.w3.org/XML/xml-names-19990114-errata">
 *        Namespaces in XML Errata</a>
 * @see <a href="http://www.w3.org/TR/xmlschema-2/#QName">
 *        XML Schema Part2: Datatypes</a>
 */

public interface NamespaceContext {

    /**
     * Get Namespace URI bound to a prefix in the current scope.
     *
     * <p>When requesting a Namespace URI by prefix, the following
     * table describes the returned Namespace URI value for all
     * possible prefix values:</p>
     *
     * <table border="2" rules="all" cellpadding="4"> 
     *   <thead>
     *     <tr>
     *       <th align="center" colspan="2">
     *         <code>getNamespaceURI(prefix)</code>
     *         return value for specified prefixes
     *       </th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <th>prefix parameter</th>
     *       <th>Namespace URI return value</th>
     *     </tr>
     *     <tr>
     *       <td><code>DEFAULT_NS_PREFIX</code> ("")</td>
     *       <td>default Namespace URI in the current scope or <code>null</code>
     *         when there is no default Namespace URI in the current scope</td>
     *     </tr>
     *     <tr>
     *       <td>bound prefix</td>
     *       <td>Namespace URI bound to prefix in current scope</td>
     *     </tr>
     *     <tr>
     *       <td>unbound prefix</td>
     *       <td><code>null</code></td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     *       <td><code>XMLConstants.XML_NS_URI</code>
     *           ("http://www.w3.org/XML/1998/namespace")</td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     *       <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     *         ("http://www.w3.org/2000/xmlns/")</td>
     *     </tr>
     *     <tr>
     *       <td><code>null</code></td>
     *       <td><code>IllegalArgumentException</code> is thrown</td>
     *     </tr>
     *    </tbody>
     * </table>
     *  
     * @param prefix prefix to look up
     * @return Namespace URI bound to prefix in the current scope
     */ 
    String getNamespaceURI(String prefix);
  
    /**
     * Get prefix bound to Namespace URI in the current scope.
     *
     * <p>To get all prefixes bound to a Namespace URI in the current
     * scope, use {@link #getPrefixes(String namespaceURI)}.</p>
     *
     * <p>When requesting a prefix by Namespace URI, the following
     * table describes the returned prefix value for all Namespace URI
     * values:</p>
     *
     * <table border="2" rules="all" cellpadding="4">
     *   <thead>
     *     <tr>
     *       <th align="center" colspan="2">
     *         <code>getPrefix(namespaceURI)</code> return value for
     *         specified Namespace URIs
     *       </th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <th>Namespace URI parameter</th>
     *       <th>prefix value returned</th>
     *     </tr>
     *     <tr>
     *       <td>&lt;default Namespace URI&gt;</td>
     *       <td><code>XMLConstants.DEFAULT_NS_PREFIX</code> ("")
     *       </td>
     *     </tr>
     *     <tr>
     *       <td>bound Namespace URI</td>
     *       <td>prefix bound to Namespace URI in the current scope,
     *           if multiple prefixes are bound to the Namespace URI in
     *           the current scope, a single arbitrary prefix, whose
     *           choice is implementation dependent, is returned</td>
     *     </tr>
     *     <tr>
     *       <td>unbound Namespace URI</td>
     *       <td><code>null</code></td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XML_NS_URI</code>
     *           ("http://www.w3.org/XML/1998/namespace")</td>
     *       <td><code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     *           ("http://www.w3.org/2000/xmlns/")</td>
     *       <td><code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     *     </tr>
     *     <tr>
     *       <td><code>""</code> or <code>null</code></td>
     *       <td><code>IllegalArgumentException</code> is thrown</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * @param namespaceURI URI of Namespace to lookup
     * @return prefix bound to Namespace URI in current context
     */
    String getPrefix(String namespaceURI);

    /**
     * Get all prefixes bound to a Namespace URI in the current scope.
     *
     * <p><strong>The returned <code>Iterator</code> is
     * <em>not</em> modifiable.  e.g. the
     * <code>remove()</code> method will throw
     * <code>NoSuchMethodException</code>.</strong></p>
     *
     * <p>Prefixes are returned in an arbitrary, implementation
     * dependent, order.</p>
     *
     * <p>When requesting prefixes by Namespace URI, the following
     * table describes the returned prefixes value for all Namespace
     * URI values:</p>
     *
     * <table border="2" rules="all" cellpadding="4">
     *   <thead>
     *     <tr>
     *       <th align="center" colspan="2"><code>
     *         getPrefixes(namespaceURI)</code> return value for
     *         specified Namespace URIs</th>
     *     </tr>
     *   </thead>
     *   <tbody>
     *     <tr>
     *       <th>Namespace URI parameter</th>
     *       <th>prefixes value returned</th>
     *     </tr>
     *     <tr>
     *       <td>bound Namespace URI,
     *         including the &lt;default Namespace URI&gt;</td>
     *       <td><code>Iterator</code> over prefixes bound to Namespace URI in
     *         the current scope in an arbitrary, implementation dependent,
     *         order</td>
     *     </tr>
     *     <tr>
     *       <td>unbound Namespace URI</td>
     *       <td>empty <code>Iterator</code></td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XML_NS_URI</code>
     *           ("http://www.w3.org/XML/1998/namespace")</td>
     *       <td><code>Iterator</code> with one element set to
     *         <code>XMLConstants.XML_NS_PREFIX</code> ("xml")</td>
     *     </tr>
     *     <tr>
     *       <td><code>XMLConstants.XMLNS_ATTRIBUTE_NS_URI</code>
     *           ("http://www.w3.org/2000/xmlns/")</td>
     *       <td><code>Iterator</code> with one element set to
     *         <code>XMLConstants.XMLNS_ATTRIBUTE</code> ("xmlns")</td>
     *     </tr>
     *     <tr>
     *       <td><code>""</code> or <code>null</code></td>
     *       <td><code>IllegalArgumentException</code> is thrown</td>
     *     </tr>
     *   </tbody>
     * </table>
     *
     * @param namespaceURI URI of Namespace to lookup
     * @return <code>Iterator</code> for all prefixes bound to the
     * Namespace URI in the current scope
     */
    Iterator getPrefixes(String namespaceURI);
}
