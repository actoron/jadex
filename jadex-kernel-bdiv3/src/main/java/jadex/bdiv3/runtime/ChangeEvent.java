package jadex.bdiv3.runtime;

/**
 * 
 */
public class ChangeEvent
{
	protected String type;
	
	protected Object source;
	
	protected Object value;

	/**
	 *  Create a new ChangeEvent. 
	 */
	public ChangeEvent()
	{
	}
	
	/**
	 *  Create a new ChangeEvent. 
	 */
	public ChangeEvent(String type, Object source, Object value)
	{
		this.type = type;
		this.source = source;
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
	 *  Get the source.
	 *  @return The source.
	 */
	public Object getSource()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(Object source)
	{
		this.source = source;
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
