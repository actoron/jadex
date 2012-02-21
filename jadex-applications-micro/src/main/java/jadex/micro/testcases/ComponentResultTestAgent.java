package jadex.micro.testcases;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
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
 *  Testing results declared in component configurations.
 */
@Description("Testing results declared in component configurations.")
@Results(@Result(name="testresults", clazz=Testcase.class))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
public class ComponentResultTestAgent extends MicroAgent
{
	/**
	 *  Perform the tests
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final TestReport	tr1	= new TestReport("#1", "Default configuration.");
		testComponentResult(null, "initial1")
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
				final TestReport	tr2	= new TestReport("#2", "Custom configuration");
				testComponentResult("config2", "initial2")
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
	protected IFuture testComponentResult(final String config, final String expected)
	{
		final Future	fut	= new Future();
		getRequiredService("cms").addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				final IComponentManagementService	cms	= (IComponentManagementService)result;
				cms.createComponent(null, "jadex/micro/testcases/Result.component.xml", new CreationInfo(config, null, getComponentIdentifier()), null)
					.addResultListener(createResultListener(new DelegationResultListener(fut)
				{
					public void customResultAvailable(Object result)
					{
						cms.destroyComponent((IComponentIdentifier)result)
							.addResultListener(new DelegationResultListener(fut)
						{
							public void customResultAvailable(Object result)
							{
								Map	results	= (Map)result;
								if(results!=null && SUtil.equals(results.get("res"), expected))
								{
									super.customResultAvailable(null);
								}
								else
								{
									throw new RuntimeException("Results do not match, expected res="+expected+" but got: "+results);
								}
							}
						});
					}					
				}));
			}
		});
		return fut;
	}
}
