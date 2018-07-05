package jadex.android.applications.demos.benchmark;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
@Agent
@Description("This agents benchmarks agent creation and termination.")
@Arguments({
	@Argument(name="max", description="Maximum number of agents to create.", clazz=int.class, defaultvalue="100"),
	@Argument(name="num", description="Number of agents already created.", clazz=int.class),
	@Argument(name="startime", description="Time when the first agent was started.", clazz=long.class),
	@Argument(name="startmem", description="Memory usage when the first agent was started", clazz=long.class)
})
public class PojoAgentCreationAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** Maximum number of agents to create. */
	@AgentArgument
	protected int	max;
	
	/** Remaining number of agents to create (-1 for start agent). */
	@AgentArgument
	protected int	num;
	
	/** Time when the first agent was started. */
	@AgentArgument
	protected long	starttime;
	
	/** Memory usage when the first agent was started. */
	@AgentArgument
	protected long	startmem;
	
	//-------- methods --------
		
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public void executeBody()
	{
		if(num==0)
		{
			getClock().addResultListener(new DefaultResultListener<IClockService>()
			{
				public void resultAvailable(IClockService result)
				{
					System.gc();
					try
					{
						Thread.sleep(500);
					}
					catch(InterruptedException e){}
					
					startmem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					starttime = ((IClockService)result).getTime();
					
					step1();
				}
			});
		}
		else
		{
			step1();
		}
	}

	/**
	 *  Execute the first step.
	 */
	protected void step1()
	{		
		num++;
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
			{
				public void resultAvailable(IComponentManagementService cms)
				{
					Map<String, Object>	args	= new HashMap<String, Object>();
					args.put("max", Integer.valueOf(max));
					args.put("num", Integer.valueOf(num));
					args.put("starttime", Long.valueOf(starttime));
					args.put("startmem", Long.valueOf(startmem));
					cms.createComponent(createPeerName(num+1, agent.getIdentifier()),
						PojoAgentCreationAgent.this.getClass().getName().replaceAll("\\.", "/")+".class",
						new CreationInfo(null, args, agent.getDescription().getResourceIdentifier()), null);
				}
			});
		}
		else
		{
			getClock().addResultListener(new DefaultResultListener<IClockService>()
			{
				public void resultAvailable(IClockService result)
				{
					final IClockService	clock	= (IClockService)result;
					final long end = clock.getTime();
					
					System.gc();
					try
					{
						Thread.sleep(500);
//						Thread.sleep(500000);
					}
					catch(InterruptedException e){}
					final long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					
					final long omem = (used-startmem)/1024;
					final double upera = ((long)(1000*(used-startmem)/max/1024))/1000.0;
					System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
					System.out.println("Last peer created. "+max+" agents started.");
					final double dur = ((double)end-starttime)/1000.0;
					final double pera = dur/max;
					System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
				
					// Use initial component to kill others
					getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
					{
						public void resultAvailable(IComponentManagementService cms)
						{
							String	initial	= createPeerName(1, agent.getIdentifier());
							IComponentIdentifier	cid	= new BasicComponentIdentifier(initial, agent.getIdentifier().getRoot());
							cms.getExternalAccess(cid).addResultListener(new DefaultResultListener<IExternalAccess>()
							{
								public void resultAvailable(IExternalAccess exta)
								{
									exta.scheduleStep(new IComponentStep<Void>()
									{
										@Classname("deletePeers")
										public IFuture<Void> execute(final IInternalAccess ia)
										{
											final Future<Void> ret = new Future<Void>();
											ia.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IClockService.class))
												.addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
											{
												public void customResultAvailable(IClockService result)
												{
													((PojoAgentCreationAgent)ia.getFeature(IPojoComponentFeature.class).getPojoAgent())
														.deletePeers(max, result.getTime(), dur, pera, omem, upera);
													ret.setResult(null);
												}
											});
											return ret;
										}
									});
								}
							});
						}
					});
				}
			});
		}
	}

	/**
	 *  Create a name for a peer with a given number.
	 */
	protected String createPeerName(int num, IComponentIdentifier cid)
	{
		String	name = cid.getLocalName();
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
	protected void deletePeers(final int cnt, final long killstarttime, final double dur, final double pera,
		final long omem, final double upera)
	{
		final String name = createPeerName(cnt, agent.getIdentifier());
		getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
		{
			public void resultAvailable(IComponentManagementService cms)
			{
				IComponentIdentifier aid = new BasicComponentIdentifier(name, agent.getIdentifier().getRoot());
				cms.destroyComponent(aid).addResultListener(new DefaultResultListener<Map<String, Object>>()
				{
					public void resultAvailable(Map<String, Object> result)
					{
						System.out.println("Successfully destroyed peer: "+name);
						
						if(cnt-1>1)
						{
							deletePeers(cnt-1, killstarttime, dur, pera, omem, upera);
						}
						else
						{
							killLastPeer(killstarttime, dur, pera, omem, upera);
						}
					}
				});
			}
		});
	}
	
	/**
	 *  Kill the last peer and print out the results.
	 */
	protected void killLastPeer(final long killstarttime, final double dur, final double pera, 
		final long omem, final double upera)
	{
		getClock().addResultListener(new DefaultResultListener<IClockService>()
		{
			public void resultAvailable(IClockService cs)
			{
				long killend = cs.getTime();
				System.out.println("Last peer destroyed. "+(max-1)+" agents killed.");
				double killdur = ((double)killend-killstarttime)/1000.0;
				final double killpera = killdur/(max-1);
				
				Runtime.getRuntime().gc();
				try
				{
					Thread.sleep(500);
				}
				catch(InterruptedException e){}
				long stillused = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
				
				System.out.println("\nCumulated results:");
				System.out.println("Creation needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
				System.out.println("Killing needed:  "+killdur+" secs. Per agent: "+killpera+" sec. Corresponds to "+(1/killpera)+" agents per sec.");
				System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
				System.out.println("Still used memory: "+stillused+"kB.");
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("microcreationtime", new Tuple(""+pera, "s"));
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("microkillingtime", new Tuple(""+killpera, "s"));
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("micromem", new Tuple(""+upera, "kb"));
				agent.killComponent();
			}
		});
	}
	
	protected IFuture<IComponentManagementService>	getCMS()
	{
		return agent.getFeature(IRequiredServicesFeature.class).getService(IComponentManagementService.class);
	}
	
	
	protected IFuture<IClockService> getClock()
	{
		return agent.getFeature(IRequiredServicesFeature.class).getService(IClockService.class);
	}
}
