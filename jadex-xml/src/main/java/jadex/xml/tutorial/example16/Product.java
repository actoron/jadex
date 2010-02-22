package jadex.xml.tutorial.example16;

import java.util.ArrayList;
import java.util.List;

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
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Product(name=" + name + ", price=" + price + ", parts=" + parts + ")";
	}	
}
