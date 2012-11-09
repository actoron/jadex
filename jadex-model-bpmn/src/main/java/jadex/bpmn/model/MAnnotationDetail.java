package jadex.bpmn.model;


/**
 *  The annotation detail class for storing the key and 
 *  value of an annotation detail.
 */
public class MAnnotationDetail extends MIdElement
{
	//-------- attributes --------
	
	/** The type. */
	protected String type;
	
	/** The key. */
	protected String key;
	
	/** The value text. */
//	protected String valuetext;
	
	/** The value. */
	protected String value;
	
	//-------- methods --------
	
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
	 *  Get the key.
	 *  @return The key.
	 */
	public String getKey()
	{
		return this.key;
	}

	/**
	 *  Set the key.
	 *  @param key The key to set.
	 */
	public void setKey(String key)
	{
		this.key = key;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 * /
	public IParsedExpression getValue()
	{
		return this.value;
	}*/

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 * /
	public void setValue(IParsedExpression value)
	{
		this.value = value;
	}*/

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
	
	
	
}
