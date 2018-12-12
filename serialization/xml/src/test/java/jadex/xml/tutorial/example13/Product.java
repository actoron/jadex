package jadex.xml.tutorial.example13;

/**
 *  A basic product description.
 */
public class Product 
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The description. */
	protected String description;
	
	/** The price. */
	protected double price;

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Get the price.
	 *  @return The price.
	 */
	public double getPrice()
	{
		return price;
	}

	/**
	 *  Set the price.
	 *  @param price The price to set.
	 */
	public void setPrice(double price)
	{
		this.price = price;
	}	
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		String ret = this.getClass().getName();
		int idx =  ret.lastIndexOf(".");
		ret = idx!=-1? ret.substring(idx+1): ret;
		return ret;
	}
	
}
