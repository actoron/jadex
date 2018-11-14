package jadex.bdi.benchmarks;

import java.util.Map;

import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IFuture;

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
				Map<String, Object> args = SCollection.createHashMap();
				args.put("max", Integer.valueOf(max));
				args.put("num", Integer.valueOf(newnum));
				args.put("counter", counter);
				args.put("starttime", starttime);
				args.put("startmem", startmem);
				args.put("parallel", Boolean.valueOf(parallel));
				
				IComponentIdentifier aid = serviceCreateAgent(createPeerName(newnum), args);
				
				System.out.println("Successfully created peer: "+aid.getLocalName());
			}
			
			if(parallel)
			{
				newnum	= newnum+1;
				
				if(newnum<=max)
				{
					Map<String, Object> args = SCollection.createHashMap();
					args.put("max", Integer.valueOf(max));
					args.put("num", Integer.valueOf(newnum));
					args.put("counter", counter);
					args.put("starttime", starttime);
					args.put("startmem", startmem);
					args.put("parallel", Boolean.valueOf(parallel));
					
					IComponentIdentifier aid	= serviceCreateAgent(createPeerName(newnum), args);
					
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
					serviceDestroyAgent(name);
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
	 *  Create an agent by directly using the CMS service.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected IComponentIdentifier serviceCreateAgent(String name, Map<String, Object> args)
	{
//		IComponentManagementService ces = getInterpreter().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM));
//		SyncResultListener lis = new SyncResultListener();
//		ces.createComponent(name, "/jadex/bdi/benchmarks/AgentCreation.agent.xml", new CreationInfo(args), lis, null);
//		IComponentIdentifier aid = (IComponentIdentifier)lis.waitForResult();
		
		IComponentIdentifier aid = getAgent().createComponent(
			new CreationInfo(null, args, getComponentDescription().getResourceIdentifier()).setName(name).setFilename("/jadex/bdi/benchmarks/AgentCreation.agent.xml"))
			.getFirstResult();
		return aid;
	}
	
	/**
	 *  Destroy an agent by directly using the CMS service.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected void serviceDestroyAgent(String name)
	{
//		IComponentManagementService ces = getInterpreter().getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, ServiceScope.PLATFORM));

//		final IComponentManagementService ces = (IComponentManagementService)getAgent()
//			.getFeature(IRequiredServicesFeature.class).getService("cms").get();
//		SyncResultListener lis = new SyncResultListener();
//		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
//		ces.destroyComponent(aid, lis);
//		lis.waitForResult();
		
//		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
		IComponentIdentifier aid = new BasicComponentIdentifier(name, getComponentIdentifier().getRoot());
		IFuture<Map<String, Object>> ret = getAgent().killComponent(aid);
		ret.get();
	}
}

