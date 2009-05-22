package jadex.commons;

/**
 *  The basic change event.
 */
public class ChangeEvent //extends EventObject
{
	//-------- attributes --------
	
	/** The source. */
	protected Object source;
	
	/** The type. */
	protected String type;
	
	/** The value. */
	protected Object value;
	
	//-------- constructors --------
	
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
	public ChangeEvent(Object source, String type, Object value)
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
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return this.value;
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
