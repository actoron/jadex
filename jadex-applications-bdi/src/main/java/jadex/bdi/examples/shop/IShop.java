package jadex.bdi.examples.shop;

import jadex.commons.IFuture;

public interface IShop
{
	/**
	 *  Buy an item.
	 *  @param item The item.
	 */
	public IFuture buyItem(String item);
}
