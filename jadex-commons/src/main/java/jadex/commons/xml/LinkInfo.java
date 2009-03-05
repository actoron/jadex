package jadex.commons.xml;

/**
 *  How to link an element described by a tag (or path fragment) to a parent object.
 */
public class LinkInfo	extends AbstractInfo
{
	//-------- attributes -------- 
	
	/** The link attribute. */
	protected Object link;
	
	//-------- constructors --------
	
	/**
	 *  Create a link info.  
	 */
	public LinkInfo(String xmlpath, Object link)
	{
		super(xmlpath);
		this.link = link;
	}
	
	//-------- methods --------

	/**
	 * Get the link attribute.
	 */
	public Object getLinkAttribute()
	{
		return this.link;
	}

	/**
	 *  Set the link attribute.
	 */
	public void setLinkAttribute(Object link)
	{
		this.link = link;
	}
}
