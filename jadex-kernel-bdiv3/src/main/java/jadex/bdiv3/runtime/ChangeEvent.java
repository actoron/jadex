package jadex.bdiv3.runtime;

/**
 * 
 */
public class ChangeEvent
{
	/** Event type that a fact has been added. */
	public static final String FACTADDED = "factadded";
	
	/** Event type that a fact has been removed. */
	public static final String FACTREMOVED = "factremoved";

	/** Event type that a fact has changed (property change in case of bean). */
	public static final String FACTCHANGED = "factchanged";

	/** Event type that a belief value has changed. */
	public static final String BELIEFCHANGED = "beliefchanged";

	
	/** Event type that a goal has been added. */
	public static final String GOALADOPTED = "goaladopted";
	
	/** Event type that a goal has been removed. */
	public static final String GOALDROPPED = "goaldropped";

	
	/** Event type that a goal has been added. */
	public static final String GOALACTIVE = "goaladopted";
	
	/** Event type that a goal has been optionized. */
	public static final String GOALOPTION = "goaloption";
	
	/** Event type that a goal has been suspended. */
	public static final String GOALSUSPENDED = "goalsuspended";

//	/** Event type that a goal has been suspended. */
//	public static final String GOALACTIVE = "goalactive";
	

	/** Event type that a goal has been added. */
	public static final String GOALINPROCESS = "goalinprocess";
	
	/** Event type that a goal has been removed. */
	public static final String GOALNOTINPROCESS = "goalnotinprocess";

	/** Event type that a goal has been added. */
	public static final String GOALINHIBITED = "goalinhibited";

	/** Event type that a goal has been added. */
	public static final String GOALNOTINHIBITED = "goalnotinhibited";

	
	/** The event type. */
	protected String type;
	
	/** The event source. */
	protected Object source;
	
	/** The event value. */
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
