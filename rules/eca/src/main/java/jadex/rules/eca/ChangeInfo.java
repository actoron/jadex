package jadex.rules.eca;

/**
 * 
 */
public class ChangeInfo<T>
{
	/** The new value. */
	protected T value;
	
	/** The old value. */
	protected T oldvalue;
	
	/** The change info, e.g. index or key. */
	protected Object info;
	
	/**
	 *  Create a new CollectionEntry.
	 */
	public ChangeInfo(T value, T oldvalue, Object info)
	{
		this.value = value;
		this.oldvalue = oldvalue;
		this.info = info;
	}

	/**
	 *  Get the value.
	 *  return The value.
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
	 *  Get the oldvalue.
	 *  return The oldvalue.
	 */
	public T getOldValue()
	{
		return oldvalue;
	}

	/**
	 *  Set the oldvalue. 
	 *  @param oldvalue The oldvalue to set.
	 */
	public void setOldValue(T oldvalue)
	{
		this.oldvalue = oldvalue;
	}

	/**
	 *  Get the info, e.g. index or key.
	 *  @return The info.
	 */
	public Object getInfo()
	{
		return info;
	}

	/**
	 *  Set the info, e.g. index or key.
	 *  @param info The info to set.
	 */
	public void setInfo(Object info)
	{
		this.info = info;
	}
	
	/**
	 * Get a string representation.
	 */
	public String	toString()
	{
		return "ChangeInfo(value="+getValue()+", old="+getOldValue()+", info="+getInfo()+")";
	}
}