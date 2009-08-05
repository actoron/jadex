package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;


/**
 *  Java bean attribute meta information.
 */
public class BeanAttributeInfo extends AttributeInfo
{	
	//-------- attributes --------
	
	// read
	
	/** The attribute value converter for reading. */
	protected ITypeConverter converterread;
	
	/** The map name (if it should be put in map). */
	protected String mapname;
	
	/** The default value. */
	protected Object defaultvalue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename)
	{
		this(xmlattributename, attributename, null, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename, ITypeConverter converter, String mapname)
	{
		this(xmlattributename, attributename, converter, mapname, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename, ITypeConverter converter, String mapname, Object defaultvalue)
	{
		super(xmlattributename, attributename!=null? attributename: xmlattributename);
		
		this.converterread = converter;
		this.mapname = mapname;
		this.defaultvalue = defaultvalue;
	}

	//-------- methods --------
	
	/**
	 *  Get the attribut name.
	 *  @return The attributename.
	 */
	public String getAttributeName()
	{
		return (String)getAttributeIdentifier();
	}

	/**
	 *  Get the attribute converter for reading.
	 *  @return The converter.
	 */
	public ITypeConverter getConverterRead()
	{
		return this.converterread;
	}

	/**
	 *  Set the map name.
	 *  For attributes that should be mapped to a map.
	 *  @return The mapname.
	 */
	public String getMapName()
	{
		return this.mapname;
	}

	/**
	 *  Get the default value.
	 *  @return the defaultvalue.
	 */
	public Object getDefaultValue()
	{
		return this.defaultvalue;
	}
}
