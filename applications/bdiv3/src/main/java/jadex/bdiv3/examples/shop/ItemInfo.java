package jadex.bdiv3.examples.shop;

/**
 *  Item info stores details about items.
 *  
 *  Note: two item infos are considered equal if there name is equal.
 *  This allows fetching old item infos and updating them by item name.
 */
public class ItemInfo
{
	//-------- attributes --------
	
	/** The name . */
	protected String name;
	
	/** The price. */
	protected double price;
	
	/** The quantity. */
	protected int quantity;
	
	//-------- constructors --------
	
	/**
	 *  Create a new item info.
	 */
	public ItemInfo()
	{
	}
	
	/**
	 *  Create a new item info.
	 */
	public ItemInfo(String name)
	{
		this(name, 0, 0);
	}
	
	/**
	 *  Create a new item info.
	 */
	public ItemInfo(String name, double price, int quantity)
	{
		this.name = name;
		this.price = price;
		this.quantity = quantity;
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

	/**
	 *  Get the quantity.
	 *  @return The quantity.
	 */
	public int getQuantity()
	{
		return quantity;
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
	 *  Get the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return ((name == null) ? 0 : name.hashCode());
	}

	/** 
	 *  Test if an object is equal to this one.
	 *  @param obj The object to test.
	 *  @return True, if both are equal.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof ItemInfo && (name.equals(((ItemInfo)obj).name));
	}
}
