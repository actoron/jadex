package jadex.xml.tutorial.example13;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple list of products.
 */
public class ProductList
{
	//-------- attributes --------

	/** The list. */
	protected List productlist;

	//-------- methods --------

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
	 *  Get the productlist.
	 *  @return The productlist.
	 */
	public List getProducts()
	{
		return productlist;
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