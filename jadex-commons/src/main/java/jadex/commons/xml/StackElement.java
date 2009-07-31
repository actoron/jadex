package jadex.commons.xml;

import java.util.Map;

/**
 *  A struct to represent an element on the stack while parsing.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The xml tag. */
	public String tag;
	
	/** The created object. */
	public Object object;
	
	/** The collected content. */
	public String content;
	
	/** The raw attributes. */
	public Map rawattrs;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object)
	{
		this(tag, object, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object, Map rawattrs)
	{
		this.tag = tag;
		this.object = object;
		this.rawattrs = rawattrs;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the tag.
	 *  @return The tag.
	 */
	public String getTag()
	{
		return this.tag;
	}

	/**
	 *  Get the object.
	 *  @return The object.
	 */
	public Object getObject()
	{
		return this.object;
	}
	
	/**
	 *  Get the content (if any).
	 *  @return The content or null for empty elements.
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 *  Get the raw attributes.
	 *  @return The raw attributes.
	 */
	public Map getRawAttributes()
	{
		return this.rawattrs;
	}

	/**
	 *  Add content to the already collected content (if any).
	 *  @param content	The content to add.
	 */
	public void	addContent(String content)
	{
		if(this.content==null)
			this.content	= content;
		else
			this.content	+= content;
	}
}