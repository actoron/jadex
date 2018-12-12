package jadex.bdiv3.runtime;

import jadex.rules.eca.IEvent;

/**
 *  Event that is thrown in case of a bdi element change (belief, goal, etc.).
 */
public class ChangeEvent<T>
{
	/** Event type that a fact has been added. */
	public static final String FACTADDED = "factadded";
	
	/** Event type that a fact has been removed. */
	public static final String FACTREMOVED = "factremoved";

	/** Event type that a fact has changed (property change in case of bean). */
	public static final String FACTCHANGED = "factchanged";

	/** Event type that a belief value has changed (the whole value was changed). */
	public static final String BELIEFCHANGED = "beliefchanged";
	
	
	/** Event type that a value has been added. */
	public static final String VALUEADDED = "valueadded";
	
	/** Event type that a value has been removed. */
	public static final String VALUEREMOVED = "valueremoved";

	/** Event type that a value has changed (property change in case of bean). */
	public static final String VALUECHANGED = "valuechanged";

	/** Event type that a parameter value has changed (the whole value was changed). */
	public static final String PARAMETERCHANGED = "parameterchanged";

	
	/** Event type that a goal has been added. */
	public static final String GOALADOPTED = "goaladopted";
	
	/** Event type that a goal has been removed. */
	public static final String GOALDROPPED = "goaldropped";

	
	/** Event type that a goal has been added. */
	public static final String GOALACTIVE = "goaladopted"; // goaladopted?! or goalactive
	
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

//	/** Event type that a goal has been added. */
//	public static final String GOALINHIBITED = "goalinhibited";
//
//	/** Event type that a goal has been added. */
//	public static final String GOALNOTINHIBITED = "goalnotinhibited";
	
	
	/** Event type that a plan has been added. */
	public static final String PLANADOPTED = "planadopted";
	
	/** Event type that a plan has been finished. */
	public static final String PLANFINISHED = "planfinished";

	
	/** The event type. */
	protected String type;
	
	/** The event source. */
	protected Object source;
	
	/** The event value. */
	protected T value;
	
	/** The change identifier, e.g. index or key. */
	protected Object info;

	/**
	 *  Create a new ChangeEvent. 
	 */
	public ChangeEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 *  @param type
	 *  @param source
	 *  @param value
	 */
	public ChangeEvent(String type, Object source, T value, Object info)
	{
		this.type = type;
		this.source = source;
		this.value = value;
		this.info = info;
	}

	/**
	 *  Create a new ChangeEvent. 
	 */
	public ChangeEvent(IEvent event)
	{
		this.type = event.getType().getType(0);
		this.source = event.getType().getType(1);
		this.value = (T) event.getContent();
	}
	
//	/**
//	 *  Create a new ChangeEvent. 
//	 */
//	public ChangeEvent(String type, Object source, Object value)
//	{
//		this.type = type;
//		this.source = source;
//		this.value = value;
//	}

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
	public T getValue()
	{
		return value;
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
	 *  Get the info.
	 *  @return The info.
	 */
	public Object getInfo()
	{
		return info;
	}

	/**
	 *  Set the info.
	 *  @param info The info to set.
	 */
	public void setInfo(Object info)
	{
		this.info = info;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "ChangeEvent [type=" + type + ", source=" + source + ", value=" + value + ", info=" + info + "]";
	}
}
