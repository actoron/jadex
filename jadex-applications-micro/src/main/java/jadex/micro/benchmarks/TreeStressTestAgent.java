package jadex.micro.benchmarks;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

/**
 *  Creates a complex tree structure of sub components.
 */
@Description("<h1>Tree Stress Test</h1>Creates a complex tree structure of sub components.")
@Arguments(@Argument(name="depth", clazz=int.class, defaultvalue="5", description="Depth of the tree."))
@Agent
public class TreeStressTestAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Execute the agent.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				int	depth	= ((Number)agent.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("depth")).intValue();
				if(depth>0)
				{
					Map	args	= new HashMap();
					args.put("depth", Integer.valueOf(depth-1));
					CreationInfo	ci	= new CreationInfo(args, agent.getComponentIdentifier());
					for(int i=0; i<depth; i++)
					{
						cms.createComponent(null, TreeStressTestAgent.this.getClass().getName()+".class", ci, null);
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
//				exception.printStackTrace();
			}
		});
		
		return new Future<Void>(); // never kill?!
	}
	
//	/**
//	 *  Info about the agent.
//	 */
//	public static Object	getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("<h1>Tree Stress Test</h1>Creates a complex tree structure of sub components.", null,
//			new IArgument[]{new Argument("depth", "Depth of the tree.", "int", Integer.valueOf(5))}, null);
//	}
}
