package jadex.micro.benchmarks;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.Tuple;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Argument;
import jadex.xml.annotation.XMLClassname;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark. 
 */
@Arguments({
	@Argument(name="num", defaultvalue="1", typename="int"),
	@Argument(name="max", defaultvalue="100", typename="int")
})
public class MegaParallelCreationAgent extends MicroAgent
{
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

		int num = args.get("num")!=null? ((Integer)args.get("num")).intValue(): 1;
		
		System.out.println("Created peer: "+num+" "+getComponentIdentifier());
		
		if(num==1)
//		if(args.get("num")==null)
		{
//			waitFor(10000, new IComponentStep()
//			{
//				public Object execute(IInternalAccess ia)
//				{
					getClock().addResultListener(createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							Long startmem = new Long(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory());
							Long starttime = new Long(((IClockService)result).getTime());
							args.put("num", new Integer(1));
							args.put("startmem", startmem);
							args.put("starttime", starttime);
							
							step1(args);
						}
					}));
//					return null;
//				}
//			});
		}
		else
		{
//			step1(args);
		}
	}

	/**
	 *  Execute the first step.
	 */
	protected void step1(final Map args)
	{
		final int num = ((Integer)args.get("num")).intValue();
		final int max = ((Integer)args.get("max")).intValue();
		
		getCMS().addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				String model = MegaParallelCreationAgent.this.getClass().getName().replaceAll("\\.", "/")+".class";
				for(int i=2; i<=max; i++)
				{
					args.put("num", new Integer(i));
					cms.createComponent(createPeerName(i, getComponentIdentifier()), model, new CreationInfo(new HashMap(args)), null)
						.addResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
					});
				}
			}
		}));
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
		final long omem, final double upera, final int max, final IMicroExternalAccess exta, final boolean nested)
	{
		final String name = createPeerName(cnt, exta.getComponentIdentifier());
//		System.out.println("Destroying peer: "+name);
		getCMS().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(final Object result)
			{
				exta.scheduleStep(new IComponentStep()
				{
					@XMLClassname("destroy1")
					public Object execute(IInternalAccess ia)
					{
						IComponentManagementService cms = (IComponentManagementService)result;
						IComponentIdentifier aid = cms.createComponentIdentifier(name, true, null);
						IResultListener lis = new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								exta.scheduleStep(new IComponentStep()
								{
									@XMLClassname("destroy2")
									public Object execute(IInternalAccess ia)
									{
										System.out.println("Successfully destroyed peer: "+name);
										
										if(cnt-1>(nested?1:1))
//										if(cnt-1>(nested?1:0))
										{
											deletePeers(cnt-1, killstarttime, dur, pera, omem, upera, max, exta, nested);
										}
										else
										{
											killLastPeer(max, killstarttime, dur, pera, omem, upera, exta);
										}
										
										return null;
									}
								});
							}
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
							}
						};
						IFuture ret = cms.destroyComponent(aid);
						ret.addResultListener(lis);
						
						return null;
					}
				});
			}
		});
	}
	
	/**
	 *  Kill the last peer and print out the results.
	 */
	protected void killLastPeer(final int max, final long killstarttime, final double dur, final double pera, 
		final long omem, final double upera, final IMicroExternalAccess exta)
	{
		getClock().addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(final Object result)
			{
				// Schedule step to run on initial component instead of last.
				exta.scheduleStep(new IComponentStep()
				{
					@XMLClassname("last")
					public Object execute(IInternalAccess ia)
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
						
						((MicroAgent)ia).setResultValue("microcreationtime", new Tuple(""+pera, "s"));
						((MicroAgent)ia).setResultValue("microkillingtime", new Tuple(""+killpera, "s"));
						((MicroAgent)ia).setResultValue("micromem", new Tuple(""+upera, "kb"));
						ia.killComponent();
						return null;
					}
				});
			}
		});
	}
	
	protected IFuture	getCMS()
	{
		IFuture cms = null;	// Uncomment for no caching.
		if(cms==null)
		{
			cms	= SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class); // Raw service
//			cms	= getRequiredService("cmsservice");	// Required service proxy
		}
		return cms;
	}
	
	
	protected IFuture	getClock()
	{
		IFuture clock = null;	// Uncomment for no caching.
		if(clock==null)
		{
			clock	= SServiceProvider.getServiceUpwards(getServiceProvider(), IClockService.class); // Raw service
//			clock	= getRequiredService("clockservice");	// Required service proxy
		}
		return clock;
	}
}
