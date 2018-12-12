package jadex.xml.tutorial.example14;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple list of products.
 */
public class ProductList
{
	//-------- attributes --------

	/** The list name. */
	protected String name;
	
	/** The list. */
	protected List productlist;

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
	 *  Add a product.
	 *  @param product The product.
	 */
	public void addProduct(Product product)
	{
		if(productlist==null)
			productlist = new ArrayList();
		productlist.add(product);
	}

	/**
	 *  Get all products.
	 *  @return All products.
	 */
	public Product[] getProducts()
	{
		return (Product[])productlist.toArray(new Product[productlist.size()]);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString() 
	{
		return "ProductList(productlist=" + productlist + ")";
	}
}