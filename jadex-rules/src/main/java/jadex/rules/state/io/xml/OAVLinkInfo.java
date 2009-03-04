package jadex.rules.state.io.xml;

import jadex.rules.state.OAVAttributeType;

/**
 *  How to link an element described by a tag (or path fragment) to a parent OAV object.
 */
public class OAVLinkInfo	extends AbstractOAVInfo
{
	//-------- attributes -------- 
	
	/** The link attribute. */
	protected OAVAttributeType link;
	
	//-------- constructors --------
	
	/**
	 *  Create a link info.  
	 */
	public OAVLinkInfo(String xmlpath, OAVAttributeType link)
	{
		super(xmlpath);
		this.link = link;
	}
	
	//-------- methods --------

	/**
	 * Get the link attribute.
	 */
	public OAVAttributeType getLinkAttribute()
	{
		return this.link;
	}

	/**
	 *  Set the link attribute.
	 */
	public void setLinkAttribute(OAVAttributeType link)
	{
		this.link = link;
	}
}
