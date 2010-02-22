package jadex.xml.tutorial.example16;

/**
 *  The part example class.
 */
public class Part
{
	//-------- attributes --------
	
	/** The product. */
	protected Product product;
	
	/** The quantity. */
	protected double quantity;

	//-------- methods --------
	
	/**
	 *  Get the product.
	 *  @return The product.
	 */
	public Product getProduct()
	{
		return this.product;
	}

	/**
	 *  Set the product.
	 *  @param product The product to set.
	 */
	public void setProduct(Product product)
	{
		this.product = product;
	}

	/**
	 *  Get the quantity.
	 *  @return The quantity.
	 */
	public double getQuantity()
	{
		return this.quantity;
	}

	/**
	 *  Set the quantity.
	 *  @param quantity The quantity to set.
	 */
	public void setQuantity(double quantity)
	{
		this.quantity = quantity;
	}
}
