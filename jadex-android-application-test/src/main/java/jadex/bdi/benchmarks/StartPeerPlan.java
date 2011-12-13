package jadex.bdi.benchmarks;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.collection.SCollection;
import jadex.commons.future.IFuture;

import java.util.Map;

import android.util.Log;
import de.unihamburg.vsis.jadexAndroid_test.Helper;

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
				
				Log.i(Helper.LOG_TAG, "Successfully created peer: "+aid.getLocalName());
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
					
					Log.i(Helper.LOG_TAG, "Successfully created peer: "+aid.getLocalName());
				}
			}
		}
		
		// Print results.
		if(counter.increment()==max)
		{
			long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			long omem = (used-startmem.longValue())/1024;
			long upera = (used-startmem.longValue())/max/1024;
			Log.i(Helper.LOG_TAG, "Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");

			long end = getTime();
			Log.i(Helper.LOG_TAG, "Last peer created. "+max+" agents started.");
			double dur = ((double)end-starttime.longValue())/1000.0;
			double pera = dur/max;
			Log.i(Helper.LOG_TAG, "Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
			
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
					Log.i(Helper.LOG_TAG, "Successfully destroyed peer: "+name);
				}
			}
			long killend = getTime();
			Log.i(Helper.LOG_TAG, "Last peer destroyed. "+(max-1)+" agents killed.");
			double killdur = ((double)killend-killstarttime)/1000.0;
			double killpera = killdur/(max-1);
			
			Runtime.getRuntime().gc();
			long stillused = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
			
			Log.i(Helper.LOG_TAG, "\nCumulated results:");
			Log.i(Helper.LOG_TAG, "Creation needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
			Log.i(Helper.LOG_TAG, "Killing needed:  "+killdur+" secs. Per agent: "+killpera+" sec. Corresponds to "+(1/killpera)+" agents per sec.");
			Log.i(Helper.LOG_TAG, "Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
			Log.i(Helper.LOG_TAG, "Still used memory: "+stillused+"kB.");

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
	protected IComponentIdentifier serviceCreateAgent(String name, Map args)
	{
		final IComponentManagementService ces = (IComponentManagementService)getServiceContainer().getRequiredService("cms").get(this);
//		SyncResultListener lis = new SyncResultListener();
//		ces.createComponent(name, "/jadex/bdi/benchmarks/AgentCreation.agent.xml", new CreationInfo(args), lis, null);
//		IComponentIdentifier aid = (IComponentIdentifier)lis.waitForResult();
		
		IFuture ret = ces.createComponent(name, "/jadex/bdi/benchmarks/AgentCreation.agent.xml", new CreationInfo(args), null);
		IComponentIdentifier aid = (IComponentIdentifier)ret.get(this);
		return aid;
	}
	
	/**
	 *  Create an agent by using the CMS capability.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected IComponentIdentifier capabilityCreateAgent(String name, Map args)
	{
		IGoal sp = createGoal("cms_create_component");
		sp.getParameter("type").setValue("/jadex/bdi/benchmarks/AgentCreation.agent.xml");
		// todo: Hack! Assumes there is no capability
		sp.getParameter("configuration").setValue(getScope().getConfigurationName());
		sp.getParameter("name").setValue(name);
		sp.getParameter("arguments").setValue(args);
		dispatchSubgoalAndWait(sp);
		return (IComponentIdentifier)sp.getParameter("componentidentifier").getValue();
	}
	
	/**
	 *  Destroy an agent by directly using the CMS service.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected void serviceDestroyAgent(String name)
	{
		final IComponentManagementService ces = (IComponentManagementService)getScope()
			.getServiceContainer().getRequiredService("cms").get(this);
//		SyncResultListener lis = new SyncResultListener();
//		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
//		ces.destroyComponent(aid, lis);
//		lis.waitForResult();
		
//		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
		IComponentIdentifier aid = new ComponentIdentifier(name, getComponentIdentifier().getRoot());
		IFuture ret = ces.destroyComponent(aid);
		ret.get(this);
	}
	
	/**
	 *  Destroy an agent by using the CMS capability.
	 *  @param name The agent instance name.
	 *  @param args The arguments.
	 */
	protected void capabilityDestroyAgent(String name)
	{
		final IComponentManagementService ces = (IComponentManagementService)getScope()
			.getServiceContainer().getRequiredService("cms").get(this);
//		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
		IComponentIdentifier aid = new ComponentIdentifier(name, getComponentIdentifier().getRoot());
		IGoal sp = createGoal("cms_destroy_component");
		sp.getParameter("componentidentifier").setValue(aid);
		dispatchSubgoalAndWait(sp);
		Log.i(Helper.LOG_TAG, "Successfully destroyed peer: "+name);
	}
}

