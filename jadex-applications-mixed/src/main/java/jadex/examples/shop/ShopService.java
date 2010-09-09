package jadex.examples.shop;

import jadex.bdi.examples.shop.IShop;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.service.BasicService;
import jadex.micro.IMicroExternalAccess;

/**
 *  The shop for buying goods at the shop.
 */
public class ShopService extends BasicService implements IShop 
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
		super(comp.getServiceProvider().getId(), IShop.class, null);

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
	public IFuture buyItem(final String item, final double price)
	{
		final Future ret = new Future();
		
		if(!isValid())
		{
			ret.setException(new RuntimeException("Service unavailable."));
		}
		else
		{
			comp.scheduleResultStep(new IResultCommand()
			{
				public Object execute(Object args)
				{
					ShopAgent agent = (ShopAgent)args;
					return agent.buyItem(item, price);
				}
			}).addResultListener(new DelegationResultListener(ret));
		}
		
		return ret;
	}
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */	
	public IFuture getCatalog()
	{
		final Future ret = new Future();
		
		if(!isValid())
		{
			ret.setException(new RuntimeException("Service unavailable."));
		}
		else
		{
			comp.scheduleResultStep(new IResultCommand()
			{
				public Object execute(Object args)
				{
					ShopAgent agent = (ShopAgent)args;
					return agent.getCatalog();
				}
			}).addResultListener(new DelegationResultListener(ret));
		}
		
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
