package jadex.micro.testcases;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Testing results declared in component configurations.
 *  Same test as in ComponentResultTest.component.xml, just as micro agent.
 */
@Description("Testing results declared in component configurations.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class ComponentResultTest2Agent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Perform the tests
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport	tr1	= new TestReport("#1", "Default configuration.");
		testComponentResult(null, "initial1")
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				tr1.setSucceeded(true);
				next();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr1.setFailed(exception.getMessage());
				next();
			}
			
			protected void next()
			{
				final TestReport	tr2	= new TestReport("#2", "Custom configuration");
				testComponentResult("config2", "initial2")
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						tr2.setSucceeded(true);
						next();
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tr2.setFailed(exception.getMessage());
						next();
					}
					
					protected void next()
					{
						agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
//						killAgent();
						ret.setResult(null);
					}
				}));
			}
		}));
		return ret;
	}

	/**
	 *  Create/destroy subcomponent and check if result is as expected.
	 */
	protected IFuture<Void> testComponentResult(final String config, final String expected)
	{
		final Future<Void>	fut	= new Future<Void>();
		agent.createComponent(new CreationInfo(config, null, agent.getId()).setFilename("jadex/micro/testcases/Result.component.xml"), null)
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(fut)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				agent.killComponent(result.getId())
					.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(fut)
				{
					public void customResultAvailable(Map<String, Object> results)
					{
						System.out.println("setting results: "+results);
						if(results!=null && SUtil.equals(results.get("res"), expected))
						{
							fut.setResult(null);
						}
						else
						{
							throw new RuntimeException("Results do not match, expected res="+expected+" but got: "+results);
						}
					}
				});
			}					
		}));
		return fut;
	}
}
