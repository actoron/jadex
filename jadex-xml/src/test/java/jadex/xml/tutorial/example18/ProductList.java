package jadex.xml.tutorial.example18;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.SUtil;

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

	//-------- constructors --------
	
	/**
	 *  Create a new productlist.
	 */
	public ProductList()
	{
	}
	
	/**
	 *  Create a new productlist.
	 */
	public ProductList(Product[] products)
	{
		setProducts(products);
//		if(products!=null)
//		{
//			for(int i=0; i<products.length; i++)
//			{
//				addProduct(products[i]);
//			}
//		}
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
	 *  Set the productlist.
	 *  @param productlist The productlist to set.
	 */
	public void setProducts(Product[] products)
	{
		if(products!=null)
		{
			for(int i=0; i<products.length; i++)
			{
				addProduct(products[i]);
			}
		}
	}
	
	/**
	 *  Get all products.
	 *  @return All products.
	 * /
	public List getProducts()
	{
		return productlist;
	}*/
	
	/**
	 *  Set the productlist.
	 *  @param productlist The productlist to set.
	 * /
	public void setProducts(List products)
	{
		this.productlist = products;
	}*/

	/**
	 *  Get the hash code.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.name == null) ? 0 : this.name.hashCode());
		result = prime * result + ((this.productlist == null) ? 0 : this.productlist.hashCode());
		return result;
	}

	/**
	 *  Test if an object is equal to this object.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = this==obj;
		
		if(!ret && obj instanceof ProductList)
		{
			ProductList pl = (ProductList)obj;
			ret = SUtil.equals(name, pl.name) && SUtil.equals(productlist, pl.productlist);
		}
		return ret;
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