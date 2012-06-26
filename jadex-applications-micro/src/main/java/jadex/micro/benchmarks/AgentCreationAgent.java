package jadex.micro.benchmarks;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
@Description("This agents benchmarks agent creation and termination.")
@Arguments(
{
	@Argument(name="max", clazz=Integer.class, defaultvalue="10000", description="Maximum number of agents to create."),
	@Argument(name="nested", clazz=Boolean.class, defaultvalue="Boolean.FALSE", description="If true, each agent is created as a subcomponent of the previous agent.")
})
public class AgentCreationAgent extends MicroAgent
{
	//-------- attributes --------
	
//	/** The cms (cached for speed). */
//	protected IFuture	cms;
//	
//	/** The clock service (cached for speed). */
//	protected IFuture	clock;
	
	//-------- methods --------
		
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
//		System.out.println("body");
		
		Map arguments = getArguments();	
		if(arguments==null)
			arguments = new HashMap();
		final Map args = arguments;	
		
		if(args.get("num")==null)
		{
//			waitFor(10000, new IComponentStep()
//			{
//				public Object execute(IInternalAccess ia)
//				{
					getClock().addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							System.gc();
							try
							{
								Thread.sleep(500);
							}
							catch(InterruptedException e){}
							
							Long startmem = new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
							Long starttime = new Long(((IClockService)result).getTime());
							args.put("num", new Integer(1));
							args.put("startmem", startmem);
							args.put("starttime", starttime);
							
							step1(args);
						}
					});
//					return null;
//				}
//			});
		}
		else
		{
			step1(args);
		}
		
		return new Future<Void>(); // never kill?!
	}

	/**
	 *  Execute the first step.
	 */
	protected void step1(final Map args)
	{
		final int num = ((Integer)args.get("num")).intValue();
		final int max = ((Integer)args.get("max")).intValue();
		final boolean nested = ((Boolean)args.get("nested")).booleanValue();
		
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			args.put("num", new Integer(num+1));
//			System.out.println("Args: "+num+" "+args);

			getCMS().addResultListener(createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					((IComponentManagementService)result).createComponent(createPeerName(num+1, getComponentIdentifier()), AgentCreationAgent.this.getClass().getName().replaceAll("\\.", "/")+".class",
						new CreationInfo(null, args, nested ? getComponentIdentifier() : null, null, null, null, null, null, null, getComponentDescription().getResourceIdentifier()), null);
				}
			}));
		}
		else
		{
			getClock().addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					final IClockService	clock	= (IClockService)result;
					final long end = clock.getTime();
					
					System.gc();
					try
					{
						Thread.sleep(500);
					}
					catch(InterruptedException e){}
					final long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					
					Long startmem = (Long)args.get("startmem");
					final Long starttime = (Long)args.get("starttime");
					final long omem = (used-startmem.longValue())/1024;
					final double upera = ((long)(1000*(used-startmem.longValue())/max/1024))/1000.0;
					System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
					System.out.println("Last peer created. "+max+" agents started.");
					final double dur = ((double)end-starttime.longValue())/1000.0;
					final double pera = dur/max;
					System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
				
					// Delete prior agents.
//					if(!nested)
//					{
//						deletePeers(max-1, clock.getTime(), dur, pera, omem, upera, max, (IMicroExternalAccess)getExternalAccess(), nested);
//					}
					
					// If nested, use initial component to kill others
//					else
					{
						getCMS().addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								IComponentManagementService	cms	= (IComponentManagementService)result;
								String	initial	= createPeerName(1, getComponentIdentifier());
//								IComponentIdentifier	cid	= cms.createComponentIdentifier(initial, true);
								IComponentIdentifier	cid	= new ComponentIdentifier(initial, getComponentIdentifier().getRoot());
								cms.getExternalAccess(cid).addResultListener(createResultListener(new DefaultResultListener()
								{
									public void resultAvailable(Object result)
									{
										IMicroExternalAccess	exta	= (IMicroExternalAccess)result;
										exta.scheduleStep(new IComponentStep<Void>()
										{
											@Classname("deletePeers")
											public IFuture<Void> execute(IInternalAccess ia)
											{
												((AgentCreationAgent)ia).deletePeers(max, clock.getTime(), dur, pera, omem, upera, max, nested);
												return IFuture.DONE;
											}
										});
									}
								}));
							}
						});
					}
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
		final long omem, final double upera, final int max, final boolean nested)
	{
		final String name = createPeerName(cnt, getComponentIdentifier());
//		System.out.println("Destroying peer: "+name);
		getCMS().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(final Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
//				IComponentIdentifier aid = cms.createComponentIdentifier(name, true, null);
				IComponentIdentifier aid = new ComponentIdentifier(name, getComponentIdentifier().getRoot());
				cms.destroyComponent(aid).addResultListener(createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						System.out.println("Successfully destroyed peer: "+name);
						
						if(cnt-1>(nested?1:1))
//										if(cnt-1>(nested?1:0))
						{
							deletePeers(cnt-1, killstarttime, dur, pera, omem, upera, max, nested);
						}
						else
						{
							killLastPeer(max, killstarttime, dur, pera, omem, upera);
						}
					}
				}));
			}
		});
	}
	
	/**
	 *  Kill the last peer and print out the results.
	 */
	protected void killLastPeer(final int max, final long killstarttime, final double dur, final double pera, 
		final long omem, final double upera)
	{
		getClock().addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(final Object result)
			{
				IClockService cs = (IClockService)result;
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
				
				setResultValue("microcreationtime", new Tuple(""+pera, "s"));
				setResultValue("microkillingtime", new Tuple(""+killpera, "s"));
				setResultValue("micromem", new Tuple(""+upera, "kb"));
				killComponent();
			}
		}));
	}
	
	protected IFuture	getCMS()
	{
		IFuture ret = null;	// Uncomment for no caching.
		if(ret==null)
		{
//			ret	= SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class);  // Raw service
			ret	= getServiceContainer().searchServiceUpwards(IComponentManagementService.class); // Decoupled service proxy
//			cms	= getRequiredService("cmsservice");	// Required service proxy
		}
		return ret;
	}
	
	
	protected IFuture getClock()
	{
		IFuture ret = null;	// Uncomment for no caching.
		if(ret==null)
		{
//			ret	= SServiceProvider.getServiceUpwards(getServiceProvider(), IClockService.class);  // Raw service
			ret	= getServiceContainer().searchServiceUpwards(IClockService.class); // Decoupled service proxy
//			clock	= getRequiredService("clockservice");	// Required service proxy
		}
		return ret;
	}
	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agents benchmarks agent creation and termination.", 
//			new String[0],
//			new IArgument[]{new Argument("max", "Maximum number of agents to create.", "Integer", new Integer(10000))
//			{
//				@XMLClassname("argument")
//				public boolean validate(String input)
//				{
//					boolean ret = true;
//					try
//					{
//						Integer.parseInt(input);
//					}
//					catch(Exception e)
//					{
//						ret = false;
//					}
//					return ret;
//				}
//			}, new Argument("nested", "If true, each agent is created as a subcomponent of the previous agent.", "boolean", Boolean.FALSE)}, 
//		null, null, null,
//		null,	// Uncomment this when using required services below.
////		new RequiredServiceInfo[]{
////			new RequiredServiceInfo("clockservice", IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM),
////			new RequiredServiceInfo("cmsservice", IComponentManagementService.class, RequiredServiceInfo.SCOPE_UPWARDS)},
//		null);
//	}
}
