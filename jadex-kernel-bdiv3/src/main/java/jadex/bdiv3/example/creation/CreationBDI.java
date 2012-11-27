package jadex.bdiv3.example.creation;

import jadex.bdiv3.BDIAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
@Agent
@Arguments(
{
	@Argument(name="num", clazz=Integer.class, defaultvalue="1", description="Agent number created."),
	@Argument(name="max", clazz=Integer.class, defaultvalue="10000", description="Maximum number of agents to create."),
})
// BDIConfigurations(@BDIConfiguration(name="first", initialplans="start"))
public class CreationBDI
{
	@AgentArgument
	protected static int num;
	
	@AgentArgument
	protected int max;
	
	@Agent
	protected BDIAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();		
		System.out.println("Created peer: "+num);
		
		if(num<max)
		{
			final Map<String, Object> args = new HashMap<String, Object>();
			args.put("num", new Integer(num+1));
			args.put("max", new Integer(max));
//			System.out.println("Args: "+num+" "+args);

			agent.getServiceContainer().searchServiceUpwards(IComponentManagementService.class).addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
			{
				public void customResultAvailable(IComponentManagementService result)
				{
					((IComponentManagementService)result).createComponent(createPeerName(num+1, agent.getComponentIdentifier()), CreationBDI.class.getName().replaceAll("\\.", "/")+".class",
							new CreationInfo(null, args, null, null, null, null, null, null, null, agent.getComponentDescription().getResourceIdentifier()), null);
				}
			});
		}
		else
		{
			System.out.println("finished");
		}
		
		return ret;
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
	
//	// todo: plan creation condition?!
//	@Plan(trigger=@Trigger(factaddeds="names"))
//	protected void printAddedFact(ChangeEvent event, RPlan rplan)
//	{
//		System.out.println("fact added: "+event.getValue()+" "+event.getSource()+" "+rplan);
//	}
	
//	public static void main(String[] args) throws Exception
//	{
//		Field f = CreationBDI.class.getDeclaredField("num");
//		f.setAccessible(true);
//		f.set(null, new Integer(1));
//		System.out.println(f.get(null));
//	}
}
