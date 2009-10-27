package jadex.bdi.planlib.ams;

import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
import jadex.bridge.AgentTerminatedException;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentListener;

/**
 *  Update the belief set containing the local agents.
 */
public class AMSLocalUpdateAgentsPlan extends Plan
{ 
	//-------- attributes --------
	
	/** The listener. */
	protected IComponentListener	listener;
	
	//-------- methods --------
	
	/**
	 *  The body method.
	 */
	public void body()
	{
		final IBeliefSet agents = getExternalAccess().getBeliefbase().getBeliefSet("agents");
		
		final Object mon = new Object();
		synchronized(mon)
		{
			this.listener	= new IComponentListener()
			{
				public void componentAdded(final IComponentDescription desc)
				{
					try
					{
						// Decouple threads to avoid deadlocks (e.g. with sync executor)
						getExternalAccess().invokeLater(new Runnable()
						{
							public void run()
							{
	//							synchronized(mon)
								{
									agents.addFact(desc);
								}
							}
						});
					}
					catch(AgentTerminatedException ate)
					{
					}
				}
						
				public void componentRemoved(final IComponentDescription desc)
				{
					try
					{
						// Decouple threads to avoid deadlocks (e.g. with sync executor)
						getExternalAccess().invokeLater(new Runnable()
						{
							public void run()
							{
	//							synchronized(mon)
								{
									agents.removeFact(desc);
								}
							}
						});
					}
					catch(AgentTerminatedException ate)
					{
					}
				}
			};
			
			IComponentExecutionService	ces	= (IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class);
			ces.addComponentListener(listener);
			
			SyncResultListener lis = new SyncResultListener();
			ces.getComponentDescriptions(lis);
			IComponentDescription[] descs = (IComponentDescription[])lis.waitForResult();
			getBeliefbase().getBeliefSet("agents").addFacts(descs);
		}
		
		// Hack!!! How to remove listener on agent exit? Store listener as belief?
		waitForEver();
	}
	
	public void aborted()
	{
		IComponentExecutionService	ces	= (IComponentExecutionService)getScope().getServiceContainer().getService(IComponentExecutionService.class);
		ces.removeComponentListener(listener);
	}
}
