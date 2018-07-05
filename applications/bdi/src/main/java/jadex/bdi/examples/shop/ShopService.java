package jadex.bdi.examples.shop;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The shop for buying goods at the shop.
 */
@Service
public class ShopService implements IShopService 
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess comp;
	
	/** The shop name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new shop service.
	 */
	public ShopService()
	{
		this.name = "noname-";
	}
	
	/**
	 *  Create a new shop service.
	 */
	public ShopService(String name)
	{
		this.name = name;
	}

	//-------- methods --------
	
	/**
	 *  Get the shop name. 
	 *  @return The name.
	 *  
	 *  @directcall (Is called on caller thread).
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Buy an item.
	 *  @param item The item.
	 */
	public IFuture<ItemInfo> buyItem(final String item, final double price)
	{
		IBDIXAgentFeature capa = comp.getFeature(IBDIXAgentFeature.class);
		
		final IGoal sell = capa.getGoalbase().createGoal("sell");
		sell.getParameter("name").setValue(item);
		sell.getParameter("price").setValue(Double.valueOf(price));
		return capa.getGoalbase().dispatchTopLevelGoal(sell);
	}
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */	
	public IFuture<ItemInfo[]> getCatalog()
	{
		IBDIXAgentFeature capa = comp.getFeature(IBDIXAgentFeature.class);
		
		final Future<ItemInfo[]> ret = new Future<ItemInfo[]>();
		ret.setResult((ItemInfo[])capa.getBeliefbase().getBeliefSet("catalog").getFacts());
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return name;
	}
}
