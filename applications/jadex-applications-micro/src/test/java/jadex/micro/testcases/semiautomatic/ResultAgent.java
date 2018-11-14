package jadex.micro.testcases.semiautomatic;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that shows how to use results.
 */
@Description("This agent starts a subagent and fetches its result.")
@Results(@Result(name="result", clazz=String.class, defaultvalue="0", description="Result value."))
@Agent
public class ResultAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Execute an agent step.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		if(Math.random()<0.3)
		{
			agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("result", "last: "+agent.getComponentIdentifier()+": "+Math.random());
//			killAgent();
			ret.setResult(null);
		}
		else
		{
			agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("result", "not last: "+agent.getComponentIdentifier()+": "+Math.random());
			
//			getServiceContainer().searchService(IComponentManagementService.class)
			SServiceProvider.getService(agent, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
				
					cms.createComponent(null, ResultAgent.this.getClass().getName()+".class", new CreationInfo(agent.getComponentIdentifier()), agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							System.out.println(agent.getComponentIdentifier()+" got result: "+result);
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
			}));
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
//			null, null, new IArgument[]{new Argument("result", "Result value.", "String", Integer.valueOf(0))}, null, null);
//	}
}
