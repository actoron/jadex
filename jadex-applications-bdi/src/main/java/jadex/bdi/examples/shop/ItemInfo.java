package jadex.bdi.examples.shop;

/**
 *  Item info stores details about items.
 */
public class ItemInfo
{
	//-------- attributes --------
	
	/** The name . */
	protected String name;
	
	/** The price. */
	protected double price;
	
	//-------- constructors --------
	
	/**
	 *  Create a new item info.
	 */
	public ItemInfo(String name, double price)
	{
		this.name = name;
		this.price = price;
	}

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return the name.
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
	 *  Get the price.
	 *  @return the price.
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
	
}
