package jadex.commons.xml;

import jadex.commons.IFilter;


/**
 * 
 */
public class SubobjectInfo extends AbstractInfo
{
	//-------- attributes --------
	
	// read + write
	
	/** The link info. */
	protected AttributeInfo linkinfo;

	//-------- constructors --------
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo)
	{
		this(linkinfo, null);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo, IFilter filter)
	{
		super(linkinfo.getXMLAttributeName(), filter);
		this.linkinfo = linkinfo;
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 * /
	public SubobjectInfo(String xmlpath, AttributeInfo linkinfo)
	{
		this(xmlpath, linkinfo, null);
	}*/
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 * /
	public SubobjectInfo(String xmlpath, AttributeInfo linkinfo, IFilter filter)
	{
		super(xmlpath, filter);
		this.linkinfo = linkinfo;
	}*/
	
	//-------- methods --------
	
	/**
	 *  Get the link info.
	 *  @return The link info.
	 */
	public AttributeInfo getLinkInfo()
	{
		return this.linkinfo;
	}
}
