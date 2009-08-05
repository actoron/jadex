package jadex.rules.state.io.xml;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.rules.state.OAVAttributeType;

/**
 *  OAV attribute meta information.
 */
public class OAVAttributeInfo extends AttributeInfo
{
	//-------- attributes --------
	
	/** The attribute value converter. */
	protected ITypeConverter converter;
	
	/** The default value. */
	protected Object defaultvalue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new oav attribute info. 
	 */
	public OAVAttributeInfo(String xmlattributename, OAVAttributeType attribute)
	{
		this(xmlattributename, attribute, null);
	}
	
	/**
	 *  Create a new oav attribute info. 
	 */
	public OAVAttributeInfo(String xmlattributename, OAVAttributeType attribute, ITypeConverter converter)
	{
		this(xmlattributename, attribute, converter, null);
	}
	
	/**
	 *  Create a new oav attribute info. 
	 */
	public OAVAttributeInfo(String xmlattributename, OAVAttributeType attribute, ITypeConverter converter, Object defaultvalue)
	{
		super(xmlattributename, attribute);
		this.converter = converter;
		this.defaultvalue = defaultvalue;
	}

	//-------- methods --------
	
	/**
	 *  Get the attribute.
	 *  @return The attribute.
	 */
	public OAVAttributeType getAttribute()
	{
		return (OAVAttributeType)getAttributeIdentifier();
	}

	/**
	 *  Set the attribute.
	 *  @param attribute The attribute to set.
	 */
	public void setAttribute(OAVAttributeType attribute)
	{
		setAttributeIdentifier(attribute);
	}

	/**
	 *  Get the attribute converter.
	 *  @return The converter.
	 */
	public ITypeConverter getConverter()
	{
		return this.converter;
	}

	/**
	 *  Set the converter.
	 *  @param converter The converter to set.
	 */
	public void setConverter(ITypeConverter converter)
	{
		this.converter = converter;
	}

	/**
	 *  Get the default value.
	 *  @return the defaultvalue.
	 */
	public Object getDefaultValue()
	{
		return this.defaultvalue;
	}

	/**
	 *  Set the default value.
	 *  @param defaultvalue the defaultvalue to set.
	 */
	public void setDefaultValue(Object defaultvalue)
	{
		this.defaultvalue = defaultvalue;
	}

}
