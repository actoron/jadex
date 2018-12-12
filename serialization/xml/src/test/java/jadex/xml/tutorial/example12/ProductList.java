package jadex.xml.tutorial.example12;

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
	 *  Get the productlist.
	 *  @return The productlist.
	 * /
	public List getSoftware()
	{
		List ret = new ArrayList();
		if(productlist!=null)
		{
			for(int i=0; i<productlist.size(); i++)
			{
				Product p = (Product)productlist.get(i);
				if(p instanceof Software)
					ret.add(p);
			}
		}
		return ret;
	}*/
	
	/**
	 *  Get the productlist.
	 *  @return The productlist.
	 * /
	public List getComputers()
	{
		List ret = new ArrayList();
		if(productlist!=null)
		{
			for(int i=0; i<productlist.size(); i++)
			{
				Product p = (Product)productlist.get(i);
				if(p instanceof Computer)
					ret.add(p);
			}
		}
		return ret;
	}*/

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString() 
	{
		return "ProductList(productlist=" + productlist + ")";
	}
}