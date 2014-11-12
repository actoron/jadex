package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collections;

/**
 *  Test searching for services that don't exist. 
 */
@Description("Test searching for services that don't exist.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class NoServiceAgent //extends MicroAgent
{
	@Agent
	protected IInternalAccess agent;
	
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport	tr	= new TestReport("#1", "Searching for services.");
		
		agent.getComponentFeature(IRequiredServicesFeature.class).searchServices(INoService.class).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				if(Collections.EMPTY_LIST.equals(result))
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("Expected empty list but was: "+result);
				}
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
//				killAgent();
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed("Exception during test: "+exception);
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
//				killAgent();
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/** Test service interface. */
	public static interface	INoService	extends IService {}
	
//	/**
//	 *  Add the 'testresults' marking this agent as a testcase. 
//	 */
//	public static Object getMetaInfo()
//	{
//		return new MicroAgentMetaInfo("Test searching for services that don't exist.", 
//			null, null, new IArgument[]{new Argument("testresults", null, "Testcase")});
//	}
}
