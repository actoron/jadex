package jadex.xml.tutorial.example20;

import jadex.commons.transformation.annotations.Exclude;


/**
 *  Invoice example class.
 */
public class Invoice
{
	//-------- attributes --------
	
	/** The invoice name. */
	protected String name;
	
	/** The description. */
	protected double price;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invoice.
	 */
	public Invoice()
	{
	}
	
	/**
	 *  Create a new invoice.
	 */
	public Invoice(String name, double price)
	{
		this.name = name;
		this.price = price;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	@Exclude
	public String getName()
	{
		return this.name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	@Exclude
	public void setName(String name)
	{
		this.name = name;
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
	 *  Test for equality.
	 *  @return True if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = this==obj;
			
		if(!ret && obj instanceof Invoice)
		{
			Invoice i = (Invoice)obj;
			
			ret = price==i.price && (name==null && i.name==null || (name!=null && name.equals(i.name)));
		}
			
		return ret;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		long temp = Double.doubleToLongBits(this.price);
		return ((this.name == null)? 0 : this.name.hashCode() + (int)(temp ^ (temp >>> 32)));
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Invoice(name=" + this.name + ", price=" + this.price + ")";
	}
	
}
