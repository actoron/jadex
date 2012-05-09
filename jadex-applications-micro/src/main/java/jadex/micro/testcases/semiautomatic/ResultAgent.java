package jadex.micro.testcases.semiautomatic;

import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that shows how to use results.
 */
@Description("This agent starts a subagent and fetches its result.")
@Results(@Result(name="result", clazz=String.class, defaultvalue="0", description="Result value."))
public class ResultAgent extends MicroAgent
{
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(Math.random()<0.3)
		{
			setResultValue("result", "last: "+getAgentName()+": "+Math.random());
//			killAgent();
			ret.setResult(null);
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
							ret.setResult(null);
//							killAgent();
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("exception occurred: "+exception);
//							killAgent();
							ret.setResult(null);
						}
					})).addResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
						}
						
						public void exceptionOccurred(Exception exception)
						{
							System.out.println("Could not create agent: "+exception);
//							killAgent();
							ret.setResult(null);
						}
					});
				}
			});
		}
		
		return ret;
	}
	
//	//-------- static methods --------
//	
//	/**
//	 *  Get the meta information about the agent.
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("This agent starts a subagent and fetches its result.", 
//			null, null, new IArgument[]{new Argument("result", "Result value.", "String", new Integer(0))}, null, null);
//	}
}
