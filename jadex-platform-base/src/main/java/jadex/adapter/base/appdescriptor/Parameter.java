package jadex.adapter.base.appdescriptor;

/**
 * 
 */
public class Parameter
{
	//-------- attributes --------

	/** The name. */
	protected String	name;

	/** The value. */
	protected String	value;

	//-------- constructors --------

	/**
	 * 
	 */
	public Parameter()
	{
	}

	//-------- methods --------

	/**
	 * @return the name
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return this.value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}
}
