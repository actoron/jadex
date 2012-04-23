package jadex.micro.examples.messagequeue;

/**
 * 
 */
public class Event
{
	/** The type. */
	protected String type;
	
	/** The value. */
	protected Object value;

	/**
	 * 
	 */
	public Event()
	{
	}

	/**
	 * 
	 */
	public Event(String type, Object value)
	{
		this.type = type;
		this.value = value;
	}

	/**
	 *  Get the type.
	 *  @return The type.
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
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
}
