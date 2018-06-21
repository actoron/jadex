package jadex.commons;

/**
 *  The basic change event.
 */
public class ChangeEvent<T> //extends EventObject
{
	//-------- attributes --------
	
	/** The source. */
	protected Object source;
	
	/** The type. */
	protected String type;
	
	/** The value. */
	protected T value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new event.
	 */
	public ChangeEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 */
	public ChangeEvent(Object source)
	{
		this(source, null);
	}
	
	/**
	 *  Create a new event.
	 */
	public ChangeEvent(Object source, String type)
	{
		this(source, type, null);
	}
	
	/**
	 *  Create a new event.
	 */
	public ChangeEvent(Object source, String type, T value)
	{
		this.source = source;
		this.type = type;
		this.value = value;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public Object getSource()
	{
		return this.source;
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
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
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
	public T getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(T value)
	{
		this.value = value;
	}

	/**
	 *  Get the string respresentation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "(Event: type="+type+", value="+value+")";
	}
}
