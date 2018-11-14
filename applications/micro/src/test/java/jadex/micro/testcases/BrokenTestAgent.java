package jadex.micro.testcases;

import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.TimeoutException;
import jadex.commons.Tuple2;
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
 *  Testing broken components.
 *  
 *  Starts the following components and verifies that they terminate with exception:
 *  - BodyExceptionAgent
 *  - ProtectedBodyAgent
 *  - PojoBodyExceptionAgent
 */
@Description("Testing broken components.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class BrokenTestAgent extends JunitAgentTest
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
		
		final TestReport	tr1	= new TestReport("#1", "Body exception subcomponent.");
		
		testBrokenComponent(BodyExceptionAgent.class.getName()+".class")
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
//				final TestReport	tr2	= new TestReport("#2", "Protected body agent.");
//				testBrokenComponent(ProtectedBodyAgent.class.getName()+".class")
//					.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
//				{
//					public void resultAvailable(Void result)
//					{
//						tr2.setSucceeded(true);
//						next();
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						tr2.setFailed(exception.getMessage());
//						next();
//					}
//					
//					protected void next()
//					{
						final TestReport	tr3	= new TestReport("#3", "PojoBodyExceptionAgent");
						testBrokenComponent(PojoBodyExceptionAgent.class.getName()+".class")
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
								agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr3}));
								ret.setResult(null);
							}
						}));
//					}
//				}));
			}
		}));
		
		return ret;
	}

	/**
	 *  Create subcomponent and check if init produces exception.
	 */
	protected IFuture<Void> testBrokenComponent(final String model)
	{
		final Future<Void>	ret	= new Future<Void>();
		IResultListener<Collection<Tuple2<String, Object>>> lis = new IResultListener<Collection<Tuple2<String, Object>>>()
		{
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
//						System.out.println("res: "+result);
				ret.setException(new RuntimeException("Terminated gracefully."));
			}
			public void exceptionOccurred(Exception exception)
			{
//						System.out.println("ex: "+exception);
				
				// Could already have exception if init has failed.
				if(exception instanceof TimeoutException)
				{
					ret.setExceptionIfUndone(exception);
				}
				else
				{
					ret.setResultIfUndone(null);
				}
			}
		};
		
		agent.createComponent(new CreationInfo(agent.getId()).setFilename(model), lis)
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(ret)
		{
			public void customResultAvailable(IExternalAccess result)
			{
			}
		});
		return ret;
	}
}
