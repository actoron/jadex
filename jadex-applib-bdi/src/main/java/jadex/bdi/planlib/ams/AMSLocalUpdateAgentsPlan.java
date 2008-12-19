package jadex.bdi.planlib.ams;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.Plan;
import jadex.bridge.AgentTerminatedException;

/**
 *  Update the belief set containing the local agents.
 */
public class AMSLocalUpdateAgentsPlan extends Plan
{ 
	//-------- attributes --------
	
	/** The listener. */
	protected IAMSListener	listener;
	
	//-------- methods --------
	
	/**
	 *  The body method.
	 */
	public void body()
	{
		IAMS ams = (IAMS)getBeliefbase().getBelief("ams").getFact();
		final IBeliefSet agents = getExternalAccess().getBeliefbase().getBeliefSet("agents");
		
		final Object mon = new Object();
		synchronized(mon)
		{
			this.listener	= new IAMSListener()
			{
				public void agentAdded(final IAMSAgentDescription desc)
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
						
				public void agentRemoved(final IAMSAgentDescription desc)
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
			
			ams.addAMSListener(listener);
			
			SyncResultListener lis = new SyncResultListener();
			ams.getAgentDescriptions(lis);
			IAMSAgentDescription[] descs = (IAMSAgentDescription[])lis.waitForResult();
			getBeliefbase().getBeliefSet("agents").addFacts(descs);
		}
		
		// Hack!!! How to remove listener on agent exit? Store listener as belief?
		waitForEver();
	}
	
	public void aborted()
	{
		IAMS ams = (IAMS)getBeliefbase().getBelief("ams").getFact();
		ams.removeAMSListener(listener);
	}
}
