package jadex.commons.xml;

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
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object)
	{
		this.tag = tag;
		this.object = object;
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