package jadex.micro.benchmarks;

import jadex.bridge.Argument;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
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
		SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
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
							public void resultAvailable(Object source, Object result)
							{
							}
						});
					}
				}
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				exception.printStackTrace();
			}
		}));
	}
	
	/**
	 *  Info about the agent.
	 */
	public static Object	getMetaInfo()
	{
		return new MicroAgentMetaInfo("<h1>Tree Stress Test</h1>Creates a complex tree structure of sub components.", null,
			new IArgument[]{new Argument("depth", "Depth of the tree.", "int", new Integer(5))}, null, null, null);
	}
}
