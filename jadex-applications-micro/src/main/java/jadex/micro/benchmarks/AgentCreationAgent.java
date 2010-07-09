package jadex.micro.benchmarks;

import jadex.bridge.ComponentResultListener;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;
import jadex.service.SServiceProvider;
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
		Map arguments = getArguments();	
		if(arguments==null)
			arguments = new HashMap();
		final Map args = arguments;	
		
		if(args.get("num")==null)
		{
			SServiceProvider.getService(getServiceProvider(), IClockService.class).addResultListener(new ComponentResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					args.put("num", new Integer(1));
//					args.put("max", new Integer(100000));
					Long startmem = new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
					Long starttime = new Long(((IClockService)result).getTime());
					args.put("startmem", startmem);
					args.put("starttime", starttime);
					
					step1(args);
				}
			}, getAgentAdapter()));
		}
		else
		{
			step1(args);
		}
	}

	/**
	 *  Execute the first step.
	 */
	protected void step1(final Map args)
	{
		final int num = ((Integer)args.get("num")).intValue();
		final int max = ((Integer)args.get("max")).intValue();
		
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			args.put("num", new Integer(num+1));
//				System.out.println("Args: "+num+" "+args);

			SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class).addResultListener(new ComponentResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					((IComponentManagementService)result).createComponent(createPeerName(num+1), AgentCreationAgent.this.getClass().getName()+".class", new CreationInfo(args), null);		
				}
			}, getAgentAdapter()));
		}
		else
		{
			Long startmem = (Long)args.get("startmem");
			final Long starttime = (Long)args.get("starttime");
			final long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
			final long omem = (used-startmem.longValue())/1024;
			final double upera = ((long)(1000*(used-startmem.longValue())/max/1024))/1000.0;
			System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");

			getTime().addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					long end = ((Long)result).longValue();
					System.out.println("Last peer created. "+max+" agents started.");
					double dur = ((double)end-starttime.longValue())/1000.0;
					double pera = dur/max;
					System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
				
					// Delete prior agents.
					deletePeers(max-1, end, dur, pera, omem, upera);
				}
			}));
		}
	}

	/**
	 *  Create a name for a peer with a given number.
	 */
	protected String createPeerName(int num)
	{
		String	name = getComponentIdentifier().getLocalName();
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
		SServiceProvider.getService(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				IComponentIdentifier aid = cms.createComponentIdentifier(name, true, null);
				IResultListener lis = createResultListener(new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
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
					public void exceptionOccurred(Object source, Exception exception)
					{
						exception.printStackTrace();
					}
				});
				IFuture ret = cms.destroyComponent(aid);
				ret.addResultListener(lis);
			}
		}));
	}
	
	/**
	 *  Kill the last peer and print out the results.
	 */
	protected void killLastPeer(final long killstarttime, final double dur, final double pera, 
		final long omem, final double upera)
	{
		getTime().addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				int max = ((Integer)getArgument("max")).intValue();
				long killend = ((Long)result).longValue();
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
				killAgent();
			}
		}));
	}
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agents benchmarks agent creation and termination.", 
			new String[0],
			new IArgument[]{new IArgument()
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
		}}, null, null);
	}
}
