package jadex.xml.tutorial.example17;

import jadex.commons.SUtil;

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

	//-------- constructors --------
	
	/**
	 *  Create a new part.
	 */
	public Part()
	{
	}
	
	/**
	 *  Create a new product.
	 */
	public Part(Product product, double quantity)
	{
		this.product = product;
		this.quantity = quantity;
	}
	
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

	/**
	 *  Get the hash code.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.product == null) ? 0 : this.product.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.quantity);
		result = prime * result + (int)(temp ^ (temp >>> 32));
		return result;
	}


	/**
	 *  Test if an object is equal to this object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = this==obj;
		
		if(!ret && obj instanceof Part)
		{
			Part p = (Part)obj;
		
			ret = SUtil.equals(product, p.product) && quantity==p.quantity;
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Part(product=" + this.product + ", quantity=" + this.quantity + ")";
	}
	
}
