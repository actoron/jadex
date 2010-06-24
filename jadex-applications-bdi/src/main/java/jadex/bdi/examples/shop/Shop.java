package jadex.bdi.examples.shop;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;

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
	public IFuture buyItem(final String item)
	{
		final Future ret = new Future();
		
		comp.createGoal("sell").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IEAGoal buy = (IEAGoal)result;
				buy.setParameterValue("name", item);
				comp.dispatchTopLevelGoalAndWait(buy).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						buy.getParameterValue("result").addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								System.out.println(comp.getComponentIdentifier().getLocalName()+" setting: "+result);
								ret.setResult(result);
							}
						});
					}
				});
			}
		});
		
		return ret;
	}
}
