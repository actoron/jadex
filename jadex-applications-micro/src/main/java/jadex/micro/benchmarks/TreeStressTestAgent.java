package jadex.micro.benchmarks;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.service.SServiceProvider;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.util.HashMap;
import java.util.Map;

/**
 *  Creates a complex tree structure of sub components.
 */
public class TreeStressTestAgent extends MicroAgent
{
	/**
	 *  Execute the agent.
	 */
	public void executeBody()
	{
		getServiceContainer().searchServiceUpwards(IComponentManagementService.class)
			.addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IComponentManagementService	cms	= (IComponentManagementService)result;
				int	depth	= ((Number)getArgument("depth")).intValue();
				if(depth>0)
				{
					Map	args	= new HashMap();
					args.put("depth", new Integer(depth-1));
					CreationInfo	ci	= new CreationInfo(args, getComponentIdentifier());
					for(int i=0; i<depth; i++)
					{
						cms.createComponent(null, TreeStressTestAgent.this.getClass().getName()+".class", ci, null)
							.addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
							}
						});
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
		});
	}
	
	/**
	 *  Info about the agent.
	 */
	public static Object	getMetaInfo()
	{
		return new MicroAgentMetaInfo("<h1>Tree Stress Test</h1>Creates a complex tree structure of sub components.", null,
			new IArgument[]{new Argument("depth", "Depth of the tree.", "int", new Integer(5))}, null);
	}
}
