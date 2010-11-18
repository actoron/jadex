package jadex.bdi.examples.shop;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;

/**
 *  The shop for buying goods at the shop.
 */
public class Shop extends BasicService implements IShop 
{
	//-------- attributes --------
	
	/** The component. */
	protected ICapability comp;
	
	/** The shop name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new shop service.
	 *  @param comp The active component.
	 */
	public Shop(ICapability comp, String name)
	{
		super(comp.getServiceProvider().getId(), IShop.class, null);

//		System.out.println("created: "+name);
		this.comp = comp;
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
	public IFuture buyItem(final String item, final double price)
	{
		final Future ret = new Future();
		
		final IGoal sell = comp.getGoalbase().createGoal("sell");
		sell.getParameter("name").setValue(item);
		sell.getParameter("price").setValue(new Double(price));
		sell.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				if(sell.isSucceeded())
					ret.setResult(sell.getParameter("result").getValue());
				else
					ret.setException(sell.getException());
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		comp.getGoalbase().dispatchTopLevelGoal(sell);
		
		return ret;
	}
	
	/**
	 *  Get the item catalog.
	 *  @return  The catalog.
	 */	
	public IFuture getCatalog()
	{
		final Future ret = new Future();
		ret.setResult(comp.getBeliefbase().getBeliefSet("catalog").getFacts());
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
