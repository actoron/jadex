package jadex.rules.eca;

/**
 *  Representation of an event.
 */
public class Event implements IEvent
{
	//-------- attributes --------
	
	/** The event type. */
	protected EventType type;
	
	/** The event content. */
	protected Object content;
	
	//-------- constructors --------

	/**
	 *  Create a new Event.
	 */
	public Event()
	{
	}
	
	/**
	 *  Create a new event.
	 */
	public Event(String type, Object content)
	{
		this(new EventType(type), content);
	}

	/**
	 *  Create a new event.
	 */
	public Event(EventType type, Object content)
	{
		this.type = type;
		this.content = content;
		
//		if(type.toString().indexOf("beliefchanged.value")!=-1 && content instanceof String)
//			System.out.println("sdfsdf");
	}
	
	//-------- methods --------

	/**
	 *  Get the type.
	 *  @return the type.
	 */
	public EventType getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(EventType type)
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
