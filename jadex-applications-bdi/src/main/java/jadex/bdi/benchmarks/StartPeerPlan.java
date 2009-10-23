package jadex.bdi.benchmarks;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.collection.SCollection;

import java.util.Map;

/**
 *  Start another peer agent.
 */
public class StartPeerPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
//		long delay = ((Integer)getBeliefbase().getBelief("delay").getFact()).intValue();
//		if(delay>0)
//			waitFor(delay);

		Counter	counter	= (Counter)getBeliefbase().getBelief("counter").getFact();
		int num = ((Integer)getBeliefbase().getBelief("num").getFact()).intValue();
		int max = ((Integer)getBeliefbase().getBelief("max").getFact()).intValue();
		Long starttime = (Long)getBeliefbase().getBelief("starttime").getFact();
		Long startmem = (Long)getBeliefbase().getBelief("startmem").getFact();
		boolean service = ((Boolean)getBeliefbase().getBelief("service").getFact()).booleanValue();
		boolean parallel = ((Boolean)getBeliefbase().getBelief("parallel").getFact()).booleanValue();

		// Create new peer.
		if(num<max)
		{
			int	newnum;
			if(parallel)
				newnum	= num*2;
			else
				newnum	= num+1;
			
			if(newnum<=max)
			{
				Map args = SCollection.createHashMap();
				args.put("max", new Integer(max));
				args.put("num", new Integer(newnum));
				args.put("counter", counter);
				args.put("starttime", starttime);
				args.put("startmem", startmem);
				args.put("parallel", new Boolean(parallel));
				
				IComponentIdentifier aid;
				if(service)
					aid = serviceCreateAgent(createPeerName(newnum), args);
				else
					aid = capabilityCreateAgent(createPeerName(newnum), args);
				
				System.out.println("Successfully created peer: "+aid.getLocalName());
			}
			
			if(parallel)
			{
				newnum	= newnum+1;
				
				if(newnum<=max)
				{
					Map args = SCollection.createHashMap();
					args.put("max", new Integer(max));
					args.put("num", new Integer(newnum));
					args.put("counter", counter);
					args.put("starttime", starttime);
					args.put("startmem", startmem);
					args.put("parallel", new Boolean(parallel));
					
					IComponentIdentifier aid;
					if(service)
						aid = serviceCreateAgent(createPeerName(newnum), args);
					else
						aid = capabilityCreateAgent(createPeerName(newnum), args);
					
					System.out.println("Successfully created peer: "+aid.getLocalName());
				}
			}
		}
		
		// Print results.
		if(counter.increment()==max)
		{
			long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			long omem = (used-startmem.longValue())/1024;
			long upera = (used-startmem.longValue())/max/1024;
			System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");

			long end = getTime();
			System.out.println("Last peer created. "+max+" agents started.");
			double dur = ((double)end-starttime.longValue())/1000.0;
			double pera = dur/max;
			System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
			
//			waitFor(300000);

			// Delete prior agents.
			long	killstarttime	= getTime();
			for(int cnt=max; cnt>0; cnt--)
			{
				if(cnt!=num)
				{
					final String	name	= createPeerName(cnt);
//					System.err.println("Destroying peer: "+name);
					if(service)
						serviceDestroyAgent(name);
					else
						capabilityDestroyAgent(name);
					System.out.println("Successfully destroyed peer: "+name);
				}
			}
			long killend = getTime();
			System.out.println("Last peer destroyed. "+(max-1)+" agents killed.");
			double killdur = ((double)killend-killstarttime)/1000.0;
			double killpera = killdur/(max-1);
			
			Runtime.getRuntime().gc();
			long stillused = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
			
			System.out.println("\nCumulated results:");
			System.out.println("Creation needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
			System.out.println("Killing needed:  "+killdur+" secs. Per agent: "+killpera+" sec. Corresponds to "+(1/killpera)+" agents per sec.");
			System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
			System.out.println("Still used memory: "+stillused+"kB.");

			killAgent();
		}
	}

	/**
	 *  Create a name for a peer with a given number.
	 */
	protected String createPeerName(int num)
	{
		String	name	= getScope().getAgentName();
		int	index	= name.indexOf("Peer_#");
		if(index!=-1)
		{
			name	= name.substring(0, index);
		}
		if(num!=1)
		{
			name	+= "Peer_#"+num;
		}
		return name;
	}
	
	/**
	 *  Create an agent by directly using the AMS service.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected IComponentIdentifier serviceCreateAgent(String name, Map args)
	{
		final IAMS ams = (IAMS)getScope().getServiceContainer().getService(IAMS.class);
		SyncResultListener lis = new SyncResultListener();
		ams.createAgent(name, "/jadex/bdi/benchmarks/AgentCreation.agent.xml", null, args, lis, getAgentIdentifier());
		IComponentIdentifier aid = (IComponentIdentifier)lis.waitForResult();
		ams.startAgent(aid, null);
		return aid;
	}
	
	/**
	 *  Create an agent by using the AMS capability.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected IComponentIdentifier capabilityCreateAgent(String name, Map args)
	{
		IGoal sp = createGoal("ams_create_agent");
		sp.getParameter("type").setValue("/jadex/bdi/benchmarks/AgentCreation.agent.xml");
		// todo: Hack! Assumes there is no capability
		sp.getParameter("configuration").setValue(getScope().getConfigurationName());
		sp.getParameter("name").setValue(name);
		sp.getParameter("arguments").setValue(args);
		dispatchSubgoalAndWait(sp);
		return (IComponentIdentifier)sp.getParameter("agentidentifier").getValue();
	}
	
	/**
	 *  Destroy an agent by directly using the AMS service.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected void serviceDestroyAgent(String name)
	{
		final IAMS ams = (IAMS)getScope().getServiceContainer().getService(IAMS.class, SFipa.AMS_SERVICE);
		SyncResultListener lis = new SyncResultListener();
		IComponentIdentifier aid = ams.createAgentIdentifier(name, true);
		ams.destroyAgent(aid, lis);
		lis.waitForResult();
	}
	
	/**
	 *  Destroy an agent by using the AMS capability.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected void capabilityDestroyAgent(String name)
	{
		final IAMS ams = (IAMS)getScope().getServiceContainer().getService(IAMS.class, SFipa.AMS_SERVICE);
		IComponentIdentifier aid = ams.createAgentIdentifier(name, true);
		IGoal sp = createGoal("ams_destroy_agent");
		sp.getParameter("agentidentifier").setValue(aid);
		dispatchSubgoalAndWait(sp);
		System.out.println("Successfully destroyed peer: "+name);
	}
}

