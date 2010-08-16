package jadex.bdi.examples.shop;

import jadex.commons.IFuture;
import jadex.service.IService;

/**
 *  The shop interface for buying goods at the shop.
 */
public interface IShop	extends IService
{
	/**
	 *  Get the shop name. 
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Buy an item.
	 *  @param item The item.
	 */
	public IFuture buyItem(String item, double price);
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */
	public IFuture getCatalog();
}
