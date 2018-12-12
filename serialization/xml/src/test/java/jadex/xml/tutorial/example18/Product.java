package jadex.xml.tutorial.example18;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SUtil;

/**
 *  A basic product description.
 */
public class Product 
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The price. */
	protected double price;
	
	/** The list of parts. */
	protected List parts;
	
	//-------- constructors --------
	
	/**
	 *  Create a new product.
	 */
	public Product()
	{
	}
	
	/**
	 *  Create a new product.
	 */
	public Product(String name, double price)
	{
		this(name, price, null);
	}
	
	/**
	 *  Create a new product.
	 */
	public Product(String name, double price, Part[] parts)
	{
		this.name = name;
		this.price = price;
		if(parts!=null)
		{
			for(int i=0; i<parts.length; i++)
			{
				addPart(parts[i]);
			}
		}
	}
	
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
	 *  Add a part.
	 *  @param part The part.
	 */
	public void addPart(Part part)
	{
		if(parts==null)
			parts = new ArrayList();
		parts.add(part);
	}

	/**
	 *  Get the parts.
	 *  @return The parts.
	 */
	public List getParts()
	{
		return this.parts;
	}
	
	/**
	 *  Set the parts.
	 *  @param parts The parts to set.
	 */
	public void setParts(List parts)
	{
		this.parts = parts;
	}

	/**
	 *  Get the hash code.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.parts == null) ? 0 : this.parts.hashCode());
		long temp;
		temp = Double.doubleToLongBits(this.price);
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
		
		if(!ret && obj instanceof Product)
		{
			Product p = (Product)obj;
		
			ret = SUtil.equals(name, p.name) && price==p.price && SUtil.equals(parts, p.parts);
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Product(name=" + name + ", price=" + price + ", parts=" + parts + ")";
	}	
}
