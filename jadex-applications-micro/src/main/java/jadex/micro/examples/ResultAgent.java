package jadex.micro.examples;

import jadex.bridge.IArgument;
import jadex.bridge.IComponentExecutionService;
import jadex.commons.concurrent.IResultListener;
import jadex.microkernel.MicroAgent;
import jadex.microkernel.MicroAgentMetaInfo;

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
			
			IComponentExecutionService ces = (IComponentExecutionService)getServiceContainer()
				.getService(IComponentExecutionService.class);
			
			ces.createComponent(null, getClass().getName()+".class", null, null, false, null, getComponentIdentifier(), createResultListener(new IResultListener()
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
			}), false);
		}
	}
	
	//-------- static methods --------
	
	/**
	 *  Get the meta information about the agent.
	 */
	public static Object getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent starts a subagent and fetches its result.", 
			null, null, new IArgument[]{
			new IArgument()
			{
				public Object getDefaultValue(String configname)
				{
					return new Integer(0);
				}
				public String getDescription()
				{
					return "Result value.";
				}
				public String getName()
				{
					return "result";
				}
				public String getTypename()
				{
					return "String";
				}
				public boolean validate(String input)
				{
					return true;
				}
			}
		}, null);
	}
}
