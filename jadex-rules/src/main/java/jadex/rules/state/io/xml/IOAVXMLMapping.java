package jadex.rules.state.io.xml;

import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVObjectType;

/**
 *  An object describing a mapping between XML elements
 *  and OAV types and attributes.
 */
public interface IOAVXMLMapping
{

	/**
	 *  Get the OAV object type corresponding
	 *  to an XML path.
	 *  @param path	The XML path (element/attribute names separated by "/").
	 *  @return The corresponding OAV object type;
	 */
	public OAVObjectType getObjectType(String path);

	/**
	 *  Get the OAV attribute type corresponding
	 *  to an XML path. The attribute type defines the attribute
	 *  of the parent object, to which the object
	 *  corresponding to the XML path is connected.  
	 *  @param path	The XML path (element/attribute names separated by "/").
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType getAttributeType(String path);

	/**
	 *  Get the OAV attribute type corresponding
	 *  to the content of an XML element.  
	 *  @param path	The XML path (element names separated by "/").
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType getContentAttributeType(String path);

	/**
	 *  Get the OAV attribute type corresponding
	 *  to the content of an XML element.  
	 *  @param type	The OAV object type to which the content should be added.
	 *  @return The corresponding OAV attribute type;
	 */
	public OAVAttributeType getContentAttributeType(OAVObjectType type);

	/**
	 *  Get the value converter for an attribute type.  
	 *  @param type	The OAV attribute type.
	 *  @return The corresponding value converter;
	 */
	public IValueConverter getValueConverter(OAVAttributeType type);

	/**
	 *  Check if an XML path is ignored in the OAV state.
	 *  @param path	The path to check.
	 */
	public boolean isIgnored(String path);

}