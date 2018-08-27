package jadex.micro.testcases.semiautomatic;

import java.util.Collection;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Tuple2;
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
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("result", "last: "+agent.getId()+": "+Math.random());
//			killAgent();
			ret.setResult(null);
		}
		else
		{
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("result", "not last: "+agent.getId()+": "+Math.random());
			
			agent.createComponent(null, new CreationInfo(agent.getId()).setFilename(ResultAgent.this.getClass().getName()+".class"), 
				agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Collection<Tuple2<String, Object>>>()
			{
				public void resultAvailable(Collection<Tuple2<String, Object>> result)
				{
					System.out.println(agent.getId()+" got result: "+result);
					ret.setResult(null);
//							killAgent();
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("exception occurred: "+exception);
//							killAgent();
					ret.setResult(null);
				}
			})).addResultListener(new IResultListener<IExternalAccess>()
			{
				public void resultAvailable(IExternalAccess result)
				{
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Could not create agent: "+exception);
//					killAgent();
					ret.setResult(null);
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
//			null, null, new IArgument[]{new Argument("result", "Result value.", "String", Integer.valueOf(0))}, null, null);
//	}
}
