package jadex.rules.eca;

/**
 *  Representation of an event.
 */
public class Event implements IEvent
{
	//-------- attributes --------
	
	/** The event type. */
	protected String type;
	
	/** The event content. */
	protected Object content;
	
	//-------- constructors --------

	/**
	 *  Create a new event.
	 */
	public Event(String type, Object content)
	{
		this.type = type;
		this.content = content;
	}
	
	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the content.
	 *  @return the content.
	 */
	public Object getContent()
	{
		return content;
	}
	
	/**
	 *  Set the content.
	 *  @param content The content to set.
	 */
	public void setContent(Object content)
	{
		this.content = content;
	}

	/** 
	 * 
	 */
	public String toString()
	{
		return "Event(type=" + type + ", content=" + content + ")";
	}
}
