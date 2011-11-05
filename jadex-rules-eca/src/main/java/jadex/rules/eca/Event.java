package jadex.rules.eca;

/**
 * 
 */
public class Event implements IEvent
{
	/** The event type. */
	protected String type;
	
	/** The event content. */
	protected Object content;
	
	/**
	 *  Create a new event.
	 */
	public Event(String type, Object content)
	{
		this.type = type;
		this.content = content;
	}

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Get the content.
	 *  @return the content.
	 */
	public Object getContent()
	{
		return content;
	}
}
