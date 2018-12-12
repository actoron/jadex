package jadex.xml.tutorial.example06;

/**
 *  Product example class.
 */
public class Product
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The type. */
	protected String type;
	
	/** The date. */
	protected String date;

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
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
	 *  Get the date.
	 *  @return The date.
	 */
	public String getDate()
	{
		return this.date;
	}

	/**
	 *  Set the date.
	 *  @param date The date to set.
	 */
	public void setDate(String date)
	{
		this.date = date;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Product(date=" + this.date + ", name=" + this.name + ", type="
			+ this.type + ")";
	}
}
