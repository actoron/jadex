package jadex.bdi.examples.shop;

import jadex.commons.IFuture;

/**
 *  The shop interface for buying goods at the shop.
 */
public interface IShop
{
	/**
	 *  Get the shop name. 
	 *  @return The name.
	 */
	public IFuture getName();
	
	/**
	 *  Buy an item.
	 *  @param item The item.
	 */
	public IFuture buyItem(String item);
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */
	public IFuture getCatalog();
}
