package jadex.commons.xml;

/**
 * 
 */
public class SubobjectInfo
{
//-------- attributes --------
	
	// read + write
	
	/** The non xml attribute. */
	protected Object attribute;

	// write
	
	/** The xml attribute name. */
	protected String xmlattributename;
	
	// todo: multiplicity
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public SubobjectInfo(Object attribute, String xmlattributename)
	{
		this.attribute = attribute;
		this.xmlattributename = xmlattributename;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the non xml attribut.
	 *  @return The attribute.
	 */
	public Object getAttribute()
	{
		return this.attribute;
	}

	/**
	 *  Set the attribute.
	 *  @param attributename the attribute to set
	 */
	public void setAttribute(String attribute)
	{
		this.attribute = attribute;
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
