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
	
}