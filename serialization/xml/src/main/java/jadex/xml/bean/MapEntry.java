package jadex.xml.bean;

/**
 *  Replacement for HashMap.Entry which is not public accessible.
 */
public class MapEntry
{
	//-------- attributes --------
	
	/** The key attributes . */
	protected Object key;
	
	/** The value attribute. */
	protected Object value;

	//-------- methods --------
	
	/**
	 *  Get the key.
	 *  @return The key.
	 */
	public Object getKey()
	{
		return this.key;
	}

	/**
	 *  Set the key.
	 *  @param key The key to set.
	 */
	public void setKey(Object key)
	{
		this.key = key;
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
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}
	
}
