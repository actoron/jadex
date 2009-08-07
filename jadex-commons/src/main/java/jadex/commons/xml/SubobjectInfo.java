package jadex.commons.xml;

import jadex.commons.IFilter;

/**
 *  Info object for subobjects, i.e. objects that are contained in another object.
 */
public class SubobjectInfo extends AbstractInfo
{
	//-------- attributes --------
	
	// read + write
	
	/** The link info. */
	protected AttributeInfo linkinfo;
	
	/** The type info of the subobjects. */
	// E.g. used for write check, i.e. is it the object of the right type
	protected TypeInfo typeinfo;

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
	 */
	public SubobjectInfo(AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo)
	{
		super(linkinfo.getXMLAttributeName(), filter);
		this.linkinfo = linkinfo;
		this.typeinfo = typeinfo;
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

	/**
	 *  Get the typeinfo.
	 *  @return The typeinfo.
	 */
	public TypeInfo getTypeInfo()
	{
		return this.typeinfo;
	}
}
