package jadex.xml.tutorial.example02;

/**
 *  Invoice example class.
 */
public class Invoice
{
	//-------- attributes --------
	
	/** The invoice name. */
	protected String name;
	
	/** The description. */
	protected String description;
	
	/** The description. */
	protected double price;
	
	/** The product key. */
	protected String key;
	
	/** The quantity. */
	protected int quantity;

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
	 *  Get the description.
	 *  @return The description.
	 */
	public String getDescription()
	{
		return this.description;
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
		return this.price;
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
	 *  Get the quantity.
	 *  @return The quantity.
	 */
	public int getQuantity()
	{
		return this.quantity;
	}

	/**
	 *  Set the quantity.
	 *  @param quantity The quantity to set.
	 */
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Invoice(description=" + this.description + ", key=" + this.key
			+ ", name=" + this.name + ", price=" + this.price
			+ ", quantity=" + this.quantity + ")";
	}
}
