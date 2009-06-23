package jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml;

/**
 * <p>
 * This file is property of DaimlerCrysler.
 * </p> 
 * 
 * AEM_UTF8AttributeImpl applies the {@link aem.util.AEM_UTF8Tools AEM_UTF8Tools} to
 * the {@link org.xml.sax.helpers.AttributesImpl org.xml.sax.helpers.AttributesImpl} 
 * In case an attributevalue is requested, the value is filtered by the UTF8Tools.   
 * @author cwiech8
 *
 */
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

/**
 * <p>
 * This file is property of DaimlerChrysler.
 * </p> 
 * 
 * 
 * @author cwiech8
 *
 */

public class AEM_UTF8AttributeImpl extends AttributesImpl {
	
	/**
	 * 
	 * Creates a new instance of AEM_UTF8AttributeImpl
	 * by copying an existing Attributes object
	 * @param attributes
	 * 						existing Attributes object
	 */
	public AEM_UTF8AttributeImpl(Attributes attributes) {
		super(attributes);
	}

	/**
	 * Look up an attribute's value by qualified (prefixed) name.
	 * @param
	 * 			<code>qName</code> The qualified name
	 * @return 
	 * 			The attribute's value, or null if there is no matching attribute.
	 */
	public String getValue(String qName) {
		return AEM_UTF8Tools.decodeUTF8(super.getValue(qName));
	}

	/**
	 * Return an attribute's value by index.
	 * 
	 * @param <code>index</code>
	 * 							The attribute's index (zero-based).
	 * 
	 * @return The attribute's value or null if the index is out of bounds.
	 * 							
	 */
	public String getValue(int index) {
		return AEM_UTF8Tools.decodeUTF8(super.getValue(index));
	}

	/**
	 * Look up an attribute's value by Namespace-qualified name.
	 * @param <code>uri</code> 
	 * 							The Namespace URI, or the empty string
	 * 							for a name with no explicit Namespace URI
	 * 
	 * @param <code>localName</code>
	 * 									The local name.
	 * 
	 * @return
	 * 			The attribute's value, or null if there is no matching attribute.
	 * 
	 */
	public String getValue(String uri, String localName) {
		return AEM_UTF8Tools.decodeUTF8(super.getValue(uri, localName));
	}
}
