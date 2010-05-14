package jadex.micro.examples;

import jadex.bridge.Argument;
import jadex.bridge.CreationInfo;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.IResultListener;
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
			
			IComponentManagementService ces = (IComponentManagementService)getServiceContainer()
				.getService(IComponentManagementService.class);
			
			ces.createComponent(null, getClass().getName()+".class", new CreationInfo(getComponentIdentifier()), createResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					System.out.println(getAgentName()+" got result: "+result);
					killAgent();
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					System.out.println("exception occurred: "+exception);
					killAgent();
				}
			}));
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts a subagent and fetches its result.", 
			null, null, new IArgument[]{new Argument("result", "Result value.", "String", new Integer(0))}, null);
	}
}
