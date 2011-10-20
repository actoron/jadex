package jadex.micro.testcases.semiautomatic;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 * 
 */
public class ResultAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public void executeBody()
	{
		if(Math.random()<0.3)
		{
			setResultValue("result", "last: "+getAgentName()+": "+Math.random());
			killAgent();
		}
		else
		{
			setResultValue("result", "not last: "+getAgentName()+": "+Math.random());
			
			getServiceContainer().searchService(IComponentManagementService.class)
				.addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
				
					cms.createComponent(null, ResultAgent.this.getClass().getName()+".class", new CreationInfo(getComponentIdentifier()), createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							System.out.println(getAgentName()+" got result: "+result);
							killAgent();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("exception occurred: "+exception);
							killAgent();
						}
					})).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Could not create agent: "+exception);
							killAgent();
						}
					});
				}
			});
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts a subagent and fetches its result.", 
			null, null, new IArgument[]{new Argument("result", "Result value.", "String", new Integer(0))}, null, null);
	}
}
