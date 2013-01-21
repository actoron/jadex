package jadex.bdiv3.benchmarks;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.runtime.RPlan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.NameValue;

import java.util.HashMap;
import java.util.Map;

/**
 *  Agent creation benchmark BDI V3.
 */
@Agent
@Description("This agents benchmarks BDI V3 agent creation and termination.")
@Arguments(
{
	@Argument(name="max", description="Maximum number of agents to create.", clazz=int.class, defaultvalue="10000"),
	@Argument(name="num", description="Number of agents already created.", clazz=int.class),
	@Argument(name="startime", description="Time when the first agent was started.", clazz=long.class),
	@Argument(name="startmem", description="Memory usage when the first agent was started", clazz=long.class)
})
@BDIConfigurations(
	@BDIConfiguration(name="first", initialplans=@NameValue(name="startPeer"))
)
public class CreationBDI
{
	//-------- attributes --------
	
	/** The component. */
	@Agent
	protected BDIAgent agent;
	
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
	
	// todo: plan creation condition
	@Plan
	protected void startPeer(RPlan rplan)
	{
		if(starttime==0)
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
	
	protected void	step1()
	{
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			final Map<String, Object> args = new HashMap<String, Object>();
			args.put("num", new Integer(num+1));
			args.put("max", new Integer(max));
			args.put("starttime", new Long(starttime));
			args.put("startmem", new Long(startmem));
//			System.out.println("Args: "+num+" "+args);

			agent.getServiceContainer().searchServiceUpwards(IComponentManagementService.class)
				.addResultListener(new DefaultResultListener<IComponentManagementService>()
			{
				public void resultAvailable(IComponentManagementService result)
				{
					((IComponentManagementService)result).createComponent(createPeerName(num+1, agent.getComponentIdentifier()), CreationBDI.class.getName().replaceAll("\\.", "/")+".class",
						new CreationInfo(null, args, null, null, null, null, null, null, null, agent.getComponentDescription().getResourceIdentifier()), null);
				}
			});
		}
		else
		{
			getClock().addResultListener(new DefaultResultListener<IClockService>()
			{
				public void resultAvailable(IClockService clock)
				{
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
				
//					// Use initial component to kill others
//					getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
//					{
//						public void resultAvailable(IComponentManagementService cms)
//						{
//							String	initial	= createPeerName(1, agent.getComponentIdentifier());
//							IComponentIdentifier	cid	= new ComponentIdentifier(initial, agent.getComponentIdentifier().getRoot());
//							cms.getExternalAccess(cid).addResultListener(new DefaultResultListener<IExternalAccess>()
//							{
//								public void resultAvailable(IExternalAccess exta)
//								{
//									exta.scheduleStep(new IComponentStep<Void>()
//									{
//										@Classname("deletePeers")
//										public IFuture<Void> execute(IInternalAccess ia)
//										{
//											((PojoAgentCreationAgent)((PojoMicroAgent)ia).getPojoAgent())
//												.deletePeers(max, clock.getTime(), dur, pera, omem, upera);
//											return IFuture.DONE;
//										}
//									});
//								}
//							});
//						}
//					});
				}
			});
		}
	}

	/**
	 *  Get the clock service.
	 */
	protected IFuture<IClockService> getClock()
	{
		return agent.getServiceContainer().searchServiceUpwards(IClockService.class);
	}

	
//	public static void main(String[] args) throws Exception
//	{
//		Field f = CreationBDI.class.getDeclaredField("num");
//		f.setAccessible(true);
//		f.set(null, new Integer(1));
//		System.out.println(f.get(null));
//	}
}
