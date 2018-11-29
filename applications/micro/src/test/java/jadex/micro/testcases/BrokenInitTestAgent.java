package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Testing broken init.
 */
@Agent
@Description("Testing broken init.")
@Results(@Result(name="testresults", clazz=Testcase.class))
public class BrokenInitTestAgent extends JunitAgentTest
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
		
		final TestReport	tr1	= new TestReport("#1", "Direct subcomponent.");
		
		testBrokenComponent(BrokenInitAgent.class.getName()+".class")
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
				final TestReport	tr2	= new TestReport("#2", "Nested subcomponent.");
				testBrokenComponent("jadex/micro/testcases/BrokenInit.component.xml")
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
						final TestReport	tr3	= new TestReport("#3", "Exception in agent created.");
						testBrokenComponent(PojoBrokenInitAgent.class.getName()+".class")
							.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
						{
							public void resultAvailable(Void result)
							{
								tr3.setSucceeded(true);
								next();
							}
							
							public void exceptionOccurred(Exception exception)
							{
								tr3.setFailed(exception.getMessage());
								next();
							}
							
							protected void next()
							{
								agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(3, new TestReport[]{tr1, tr2, tr3}));
								ret.setResult(null);
								//killAgent();
							}
						}));
					}
				}));
			}
		}));
		
		return ret;
	}
	

	/**
	 *  Create subcomponent and check if init produces exception.
	 */
	protected IFuture<Void> testBrokenComponent(final String model)
	{
		final Future<Void>	fut1	= new Future<Void>();
		agent.createComponent(new CreationInfo().setFilename(model))
			.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IExternalAccess>()
		{
			public void resultAvailable(IExternalAccess result)
			{
				fut1.setException(new RuntimeException("Creation unexpectedly succeded."));
				agent.getExternalAccess(result.getId()).killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(exception.getMessage().equals("Exception in init."))
				{
					fut1.setResult(null);
				}
				else
				{
					fut1.setException(exception);
				}
			}
		}));
		return fut1;
	}
}
