package jadex.commons.xml;

/**
 *  Java bean attribute meta information.
 */
public class BeanAttributeInfo
{
	//-------- attributes --------
	
	/** The Java attribute name. */
	protected String attributename;
	
	/** The attribute value converter. */
	protected ITypeConverter converter;
	
	/** The map name (if it should be put in map). */
	protected String mapname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename)
	{
		this(attributename, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanAttributeInfo(String attributename, ITypeConverter converter, String mapname)
	{
		this.attributename = attributename;
		this.converter = converter;
		this.mapname = mapname;
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
}
