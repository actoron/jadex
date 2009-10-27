package jadex.micro.benchmarks;

import jadex.bridge.IArgument;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;
import jadex.service.clock.IClockService;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
public class AgentCreationAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The step indicating what should the agent do. */
//	protected int step;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		Map args = getArguments();	
		if(args==null)
			args = new HashMap();
		
		if(args.get("num")==null)
		{
			args.put("num", new Integer(1));
//				args.put("max", new Integer(100000));
			Long startmem = new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
			Long starttime = new Long(((IClockService)getServiceContainer().getService(IClockService.class)).getTime());
			args.put("startmem", startmem);
			args.put("starttime", starttime);
		}
		
		int num = ((Integer)args.get("num")).intValue();
		int max = ((Integer)args.get("max")).intValue();
		
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			args.put("num", new Integer(num+1));
//				System.out.println("Args: "+num+" "+args);

			final IComponentExecutionService ces = (IComponentExecutionService)getServiceContainer().getService(IComponentExecutionService.class);
			ces.createComponent(createPeerName(num+1), getClass().getName()+".class", null, args, 
				createResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					ces.startComponent((IComponentIdentifier)result, null);
				}
				public void exceptionOccurred(Exception exception)
				{
					exception.printStackTrace();
				}
			}), getAgentIdentifier());				
		}
		else
		{
			Long startmem = (Long)args.get("startmem");
			Long starttime = (Long)args.get("starttime");
			long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			long omem = (used-startmem.longValue())/1024;
			double upera = ((long)(1000*(used-startmem.longValue())/max/1024))/1000.0;
			System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");

			long end = getTime();
			System.out.println("Last peer created. "+max+" agents started.");
			double dur = ((double)end-starttime.longValue())/1000.0;
			double pera = dur/max;
			System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
		
			// Delete prior agents.
			deletePeers(max-1, getTime(), dur, pera, omem, upera);
		}
	}
	
	/**
	 *  Create a name for a peer with a given number.
	 */
	protected String createPeerName(int num)
	{
		String	name = getAgentIdentifier().getLocalName();
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
	 *  Delete all peers from last-1 to first.
	 *  @param cnt The highest number of the agent to kill.
	 */
	protected void deletePeers(final int cnt, final long killstarttime, final double dur, final double pera, final long omem, final double upera)
	{
		final String name = createPeerName(cnt);
//		System.out.println("Destroying peer: "+name);
		final IComponentExecutionService ces = (IComponentExecutionService)getServiceContainer().getService(IComponentExecutionService.class);
		IComponentIdentifier aid = ces.createComponentIdentifier(name, true, null);
		ces.destroyComponent(aid, createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println("Successfully destroyed peer: "+name);
				
				if(cnt-1>0)
				{
					deletePeers(cnt-1, killstarttime, dur, pera, omem, upera);
				}
				else
				{
					killLastPeer(killstarttime, dur, pera, omem, upera);
				}	
			}
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Kill the last peer and print out the results.
	 */
	protected void killLastPeer(long killstarttime, double dur, double pera, long omem, double upera)
	{
		int max = ((Integer)getArgument("max")).intValue();
		
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

		// Todo: killAgent()
		getAgentAdapter().killAgent();
	}
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agents benchmarks agent creation and termination.", 
			new String[0], new IArgument[]{new IArgument()
		{
			public Object getDefaultValue(String configname)
			{
				return new Integer(10000);
			}
			public String getDescription()
			{
				return "Maximum number of agents to create.";
			}
			public String getName()
			{
				return "max";
			}
			public String getTypename()
			{
				return "Integer";
			}
			public boolean validate(String input)
			{
				boolean ret = true;
				try
				{
					Integer.parseInt(input);
				}
				catch(Exception e)
				{
					ret = false;
				}
				return ret;
			}
		}});
	}
}
