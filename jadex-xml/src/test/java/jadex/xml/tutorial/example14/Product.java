package jadex.xml.tutorial.example14;

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
	
	/** The containing product list. */
	protected ProductList productlist;

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
	 *  Get the productlist.
	 *  @return The productlist.
	 */
	public ProductList getProductlist()
	{
		return productlist;
	}

	/**
	 *  Set the productlist.
	 *  @param productlist The productlist to set.
	 */
	public void setProductlist(ProductList productlist)
	{
		this.productlist = productlist;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Product(description=" + description + ", name=" + name
			+ ", price=" + price + ", productlist=" + (productlist!=null? productlist.getName(): null) + ")";
	}	
}
