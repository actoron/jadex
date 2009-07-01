package jadex.commons.xml;

import jadex.commons.IFilter;

/**
 *  How to link an element described by a tag (or path fragment) to a parent object.
 */
public class LinkInfo	extends AbstractInfo
{
	//-------- attributes -------- 
	
	/** The link attribute. */
	protected Object linkinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public LinkInfo(String xmlpath, Object linkinfo)
	{
		this(xmlpath, linkinfo, null);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public LinkInfo(String xmlpath, Object linkinfo, IFilter filter)
	{
		super(xmlpath, filter);
		this.linkinfo = linkinfo;
	}
	
	//-------- methods --------

	/**
	 *  Get the link info.
	 *  @return The link info.
	 */
	public Object getLinkInfo()
	{
		return this.linkinfo;
	}

	/**
	 *  Set the link info.
	 *  @param linkinfo The link info.
	 */
	public void setLinkInfo(Object linkinfo)
	{
		this.linkinfo = linkinfo;
	}
}
