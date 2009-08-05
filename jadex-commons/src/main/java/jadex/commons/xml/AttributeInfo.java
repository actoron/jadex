package jadex.commons.xml;

/**
 *  Base class for attribute infos.
 */
public class AttributeInfo
{
	//-------- attributes --------
	
	/** The object attribute. */
	protected Object attributeidentifier;

	/** The xml attribute name. */
	protected String xmlattributename;
	
	//-------- constructors --------
		
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier)
	{
		this.xmlattributename = xmlattributename;
		this.attributeidentifier = attributeidentifier;
	}

	//-------- methods --------
	
	/**
	 *  Get the attributeidentifier.
	 *  @return The attributeidentifier.
	 */
	public Object getAttributeIdentifier()
	{
		return this.attributeidentifier;
	}

	/**
	 *  Set the attributeidentifier.
	 *  @param attributeidentifier The attributeidentifier to set.
	 */
	public void setAttributeIdentifier(Object attributeidentifier)
	{
		this.attributeidentifier = attributeidentifier;
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
