package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Testing broken init.
 */
@Description("Testing broken init.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class BrokenInitTestAgent extends MicroAgent
{
	/**
	 *  Perform the tests
	 */
	public void executeBody()
	{
		final TestReport	tr1	= new TestReport("#1", "Direct subcomponent.");
		testBrokenComponent(BrokenInitAgent.class.getName()+".class")
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
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
					.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
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
						setResultValue("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
						killAgent();
					}
				}));
			}
		}));
	}

	/**
	 *  Create subcomponent and check if init produces exception.
	 */
	protected IFuture testBrokenComponent(final String model)
	{
		final Future	fut1	= new Future();
		getRequiredService("cms").addResultListener(new DelegationResultListener(fut1)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.createComponent(null, model, new CreationInfo(getComponentIdentifier()), null)
					.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						fut1.setException(new RuntimeException("Creation unexpectedly succeded."));
						cms.destroyComponent((IComponentIdentifier) result);
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
