package jadex.commons.xml;

/**
 * 
 */
public class BeanSubobjectInfo
{
//-------- attributes --------
	
	// read + write
	
	/** The Java attribute name. */
	protected String attributename;

	// write
	
	/** The xml attribute name. */
	protected String xmlattributename;
	
	// todo: multiplicity
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public BeanSubobjectInfo(String attributename, String xmlattributename)
	{
		this.attributename = attributename;
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
