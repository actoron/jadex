package jadex.micro.benchmarks;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Boolean3;
import jadex.commons.Tuple;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 *  Agent creation benchmark. 
 */
@Arguments({
	@Argument(name="max", defaultvalue="20000", clazz=int.class)
})
@Agent(synchronous=Boolean3.FALSE)
public class MegaParallelStarterAgent
{
	@Agent
	protected IInternalAccess agent;
	
	protected String subname;
	
	protected int agents;
	
	protected long startmem;
	protected long starttime;
	protected long omem;
	protected double dur;
	protected double pera;
	protected double upera;
	
	protected long killstarttime;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		Future<Void> ret = new Future<Void>();
		
		Map arguments = agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments();	
		if(arguments==null)
			arguments = new HashMap();
		final Map args = arguments;	

		System.out.println("Created starter: "+agent.getComponentIdentifier());
		this.subname = "peer";
		
		getClock().addResultListener(new ExceptionDelegationResultListener<IClockService, Void>(ret)
		{
			public void customResultAvailable(IClockService result)
			{
				startmem = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
				starttime = ((IClockService)result).getTime();
				
				final int max = ((Integer)args.get("max")).intValue();
				
				IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
				String model = MegaParallelCreationAgent.class.getName().replaceAll("\\.", "/")+".class";
				for(int i=1; i<=max; i++)
				{
					args.put("num", Integer.valueOf(i));
//							System.out.println("Created agent: "+i);
					cms.createComponent(subname+"_#"+i, model, new CreationInfo(new HashMap(args), agent.getComponentIdentifier()), 
						agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
					{
						public void resultAvailable(Object result)
						{
							if(--agents==0)
							{
								getClock().addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(final Object result)
									{
										IClockService cs = (IClockService)result;
										long killend = cs.getTime();
										System.out.println("Last peer destroyed. "+(max-1)+" agents killed.");
										double killdur = ((double)killend-killstarttime)/1000.0;
										final double killpera = killdur/(max-1);
										
										long stillused = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024;
										
										System.out.println("\nCumulated results:");
										System.out.println("Creation needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");
										System.out.println("Killing needed:  "+killdur+" secs. Per agent: "+killpera+" sec. Corresponds to "+(1/killpera)+" agents per sec.");
										System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
										System.out.println("Still used memory: "+stillused+"kB.");
										
										agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("microcreationtime", new Tuple(""+pera, "s"));
										agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("microkillingtime", new Tuple(""+killpera, "s"));
										agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("micromem", new Tuple(""+upera, "kb"));
										agent.killComponent();
									}
								});
							}
						}
					})).addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							if(++agents==max)
							{
								getClock().addResultListener(new DefaultResultListener()
								{
									public void resultAvailable(final Object result)
									{
										final IClockService	clock	= (IClockService)result;
										final long end = clock.getTime();
										final long used = Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
										omem = (used-startmem)/1024;
										upera = ((long)(1000*(used-startmem/max/1024)))/1000.0;
										System.out.println("Overall memory usage: "+omem+"kB. Per agent: "+upera+" kB.");
										System.out.println("Last peer created. "+max+" agents started.");
										dur = ((double)end-starttime)/1000.0;
										pera = dur/max;
										System.out.println("Needed: "+dur+" secs. Per agent: "+pera+" sec. Corresponds to "+(1/pera)+" agents per sec.");

										killstarttime = clock.getTime();
										deletePeers(max);
									}
								});
							}
						}
						public void exceptionOccurred(Exception exception)
						{
							// ignore
							// In case of ComponentStartTest the agent will be started
							// and immediately terminated but already has scheduled
							// all creation actions to the cms.
						}
					}));
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Delete all peers from last-1 to first.
	 *  @param cnt The highest number of the agent to kill.
	 */
	protected void deletePeers(final int cnt)
	{
		final String name = subname+"_#"+cnt;
//		System.out.println("Destroying peer: "+name);
		IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		IComponentIdentifier aid = new BasicComponentIdentifier(name, agent.getComponentIdentifier());
		IResultListener lis = new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				System.out.println("Successfully destroyed peer: "+name);
				
				if(cnt>1)
				{
					deletePeers(cnt-1);
				}
			}
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		};
		IFuture ret = cms.destroyComponent(aid);
		ret.addResultListener(lis);
	}
	
//	protected IFuture<IComponentManagementService>	getCMS()
//	{
//		IFuture<IComponentManagementService> cms = null;	// Uncomment for no caching.
//		if(cms==null)
//		{
//			cms	= agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class); // Raw service
////			cms	= getService("cmsservice");	// Required service proxy
//		}
//		return cms;
//	}
	
	
	protected IFuture<IClockService>	getClock()
	{
		IFuture<IClockService> clock = null;	// Uncomment for no caching.
		if(clock==null)
		{
			clock	= agent.getComponentFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)); // Raw service
//			clock	= getService("clockservice");	// Required service proxy
		}
		return clock;
	}

}
