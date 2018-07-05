package jadex.micro.testcases;

import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Testing broken init.
 */
@Agent
@Description("Testing broken init.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
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
		IFuture<IComponentManagementService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(fut1)
		{
			@SuppressWarnings("deprecation")
			public void customResultAvailable(final IComponentManagementService cms)
			{
				cms.createComponent(null, model, new CreationInfo(agent.getIdentifier()), new IResultListener<Collection<Tuple2<String,Object>>>()
				{
					// Dummy listener to avoid fatal error being printed.
					@Override
					public void exceptionOccurred(Exception exception){}
					@Override					
					public void resultAvailable(Collection<Tuple2<String,Object>> result) {};
				})
					.addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<IComponentIdentifier>()
				{
					public void resultAvailable(IComponentIdentifier result)
					{
						fut1.setException(new RuntimeException("Creation unexpectedly succeded."));
						cms.destroyComponent(result);
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
			}
		});
		return fut1;
	}
}
