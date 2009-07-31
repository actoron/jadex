package jadex.commons.xml;


/**
 *  Java bean attribute meta information.
 */
public class BeanAttributeInfo
{	
	//-------- attributes --------
	
	// read + write
	
	/** The Java attribute name. */
	protected String attributename;

	// write
	
	protected String xmlattributename;
	
	// read
	
	/** The attribute value converter. */
	protected ITypeConverter converter;
	
	/** The map name (if it should be put in map). */
	protected String mapname;
	
	/** The default value. */
	protected Object defaultvalue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename)
	{
		this(attributename, (String)null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, String xmlattributename)
	{
		this(attributename, null, null, null, xmlattributename);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, ITypeConverter converter)
	{
		this(attributename, converter, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, ITypeConverter converter, String mapname)
	{
		this(attributename, converter, mapname, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, ITypeConverter converter, String mapname, Object defaultvalue)
	{
		this(attributename, converter, mapname, defaultvalue, null);
	}
		
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, ITypeConverter converter, String mapname, Object defaultvalue, String xmlattributename)
	{
		this.attributename = attributename;
		this.converter = converter;
		this.mapname = mapname;
		this.defaultvalue = defaultvalue;
		this.xmlattributename = xmlattributename;
	}

	//-------- methods --------
	
	/**
	 *  Get the attribut name.
	 *  @return The attributename.
	 */
	public String getAttributeName()
	{
		return this.attributename;
	}

	/**
	 *  Set the attribute name.
	 *  @param attributename the attributename to set
	 */
	public void setAttributeName(String attributename)
	{
		this.attributename = attributename;
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
	 *  Set the map name.
	 *  For attributes that should be mapped to a map.
	 *  @return The mapname.
	 */
	public String getMapName()
	{
		return this.mapname;
	}

	/**
	 *  Set the mapname.
	 *  For attributes that should be mapped to a map.
	 *  @param mapname the mapname to set.
	 */
	public void setMapname(String mapname)
	{
		this.mapname = mapname;
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
	
	
	/**
	 *  Get the attribut name.
	 *  @return The attributename.
	 */
	public String getXMLAttributeName()
	{
		return this.xmlattributename;
	}

	/**
	 *  Set the attribute name.
	 *  @param xmlattributename the xmlattributename to set
	 */
	public void setXMLAttributeName(String xmlattributename)
	{
		this.xmlattributename = xmlattributename;
	}

}
