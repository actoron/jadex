package jadex.examples.shop;

import jadex.bdi.examples.shop.IShopService;
import jadex.bdi.examples.shop.ItemInfo;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.commons.future.IFuture;
import jadex.micro.IMicroExternalAccess;


/**
 *  The shop for buying goods at the shop.
 */
public class ShopService extends BasicService implements IShopService
{
	//-------- attributes --------
	
	/** The component. */
	protected IMicroExternalAccess comp;
	
	/** The shop name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new shop service.
	 *  @param comp The active component.
	 */
	public ShopService(IExternalAccess comp, String name)
	{
		super(comp.getServiceProvider().getId(), IShopService.class, null);

//		System.out.println("created: "+name);
		this.comp = (IMicroExternalAccess)comp;
		this.name = name;
	}

	//-------- methods --------
	
	/**
	 *  Get the shop name. 
	 *  @return The name.
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
		return comp.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ShopAgent agent = (ShopAgent)ia;
				return agent.buyItem(item, price);
			}
		});
	}
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */	
	public IFuture<ItemInfo[]> getCatalog()
	{
		return comp.scheduleStep(new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				ShopAgent agent = (ShopAgent)ia;
				return agent.getCatalog();
			}
		});
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
