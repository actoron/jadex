package jadex.bdi.examples.shop;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.service.BasicService;

/**
 *  The shop for buying goods at the shop.
 */
public class Shop extends BasicService implements IShop 
{
	//-------- attributes --------
	
	/** The component. */
	protected IBDIExternalAccess comp;
	
	/** The shop name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new shop service.
	 *  @param comp The active component.
	 */
	public Shop(IExternalAccess comp, String name)
	{
		super(BasicService.createServiceIdentifier(comp.getServiceProvider().getId(), Shop.class));

//		System.out.println("created: "+name);
		this.comp = (IBDIExternalAccess)comp;
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
			comp.createGoal("sell").addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					final IEAGoal buy = (IEAGoal)result;
					buy.setParameterValue("name", item);
					buy.setParameterValue("price", new Double(price));
					comp.dispatchTopLevelGoalAndWait(buy).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object source, Object result)
						{
							buy.getParameterValue("result").addResultListener(new DelegationResultListener(ret));
						}
						
						public void exceptionOccurred(Object source, Exception exception)
						{
							ret.setException(exception);
						}
					});
				}
			});
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
			comp.getBeliefbase().getBeliefSetFacts("catalog").addResultListener(new DelegationResultListener(ret));
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
