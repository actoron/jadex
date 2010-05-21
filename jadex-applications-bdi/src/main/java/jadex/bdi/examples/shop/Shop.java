package jadex.bdi.examples.shop;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;

/**
 * 
 */
public class Shop implements IShop 
{
	/** The component. */
	protected IBDIExternalAccess comp;
	
	/**
	 * 
	 */
	public Shop(IExternalAccess comp)
	{
		this.comp = (IBDIExternalAccess)comp;
	}

	/**
	 * 
	 */
	public IFuture buyItem(String item)
	{
		final Future ret = new Future();
		final IGoal buy = comp.createGoal("sell");
		buy.getParameter("name").setValue(item);
		buy.addGoalListener(new IGoalListener()
		{
			public void goalFinished(AgentEvent ae)
			{
				ret.setResult(buy.getParameter("result").getValue());
			}
			
			public void goalAdded(AgentEvent ae)
			{
			}
		});
		comp.dispatchTopLevelGoal(buy);
		
		return ret;
	}
}
