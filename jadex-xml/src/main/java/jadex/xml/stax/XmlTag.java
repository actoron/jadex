package jadex.xml.stax;

public class XmlTag
{
	/** The name space URI. */
	protected String namespaceuri;
	
	/** The local part. */
	protected String localpart;
	
	public XmlTag(String namespaceuri, String localpart)
	{
		this.namespaceuri = namespaceuri;
		this.localpart = localpart;
	}
	
	/**
     *  Returns the namespace URI of the current element.
     * 
     *  @return The namespace.
     */
	public String getNamespace()
	{
		return namespaceuri;
	}
	
	/**
	 *  Returns the local part of the element.
	 *  
	 *  @return Local part.
	 */
	public String getLocalPart()
	{
		return localpart;
	}

	/**
	 *  Get a string representation.
	 */
	public String toString()
	{
		return "<"+getLocalPart()+">";
	}
}
