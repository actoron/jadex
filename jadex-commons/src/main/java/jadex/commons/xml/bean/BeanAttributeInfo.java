package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;


/**
 *  Java bean attribute meta information.
 */
public class BeanAttributeInfo extends AttributeInfo
{	
	//-------- attributes --------
	
	// read + write
	
	/** The default value. */
	protected Object defaultvalue;
	
	/** The map name (if it should be put in map). */
	protected String mapname; // todo: exploit also for writing?!

	// read
	
	/** The attribute value converter for reading. */
	protected ITypeConverter converterread;
	
	// write
	
	/** The attribute value converter for write. */
	protected ITypeConverter converterwrite;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename)
	{
		this(xmlattributename, attributename, null, null, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename, String ignore)
	{
		this(xmlattributename, attributename, ignore, null, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, String mapname)
	{
		this(xmlattributename, attributename, ignore, converterread, null, mapname, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String xmlattributename, String attributename, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, String mapname, Object defaultvalue)
	{
		super(xmlattributename, attributename!=null? attributename: xmlattributename, ignore);
		
		this.converterread = converterread;
		this.converterwrite = converterwrite;
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
	 *  Get the attribute converter for writing.
	 *  @return The converter.
	 */
	public ITypeConverter getConverterWrite()
	{
		return this.converterwrite;
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
