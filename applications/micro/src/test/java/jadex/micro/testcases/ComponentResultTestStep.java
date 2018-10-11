package jadex.micro.testcases;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentStep;
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

/**
 *  Behavior of the component result test.
 */
public class ComponentResultTestStep implements IComponentStep<Void>
{
	/**
	 *  Execute the test.
	 */
	public IFuture<Void> execute(final IInternalAccess ia)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		final TestReport	tr1	= new TestReport("#1", "Default configuration.");
		testComponentResult(null, "initial1", ia)
			.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
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
				testComponentResult("config2", "initial2", ia)
					.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new IResultListener<Void>()
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
						ia.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
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
	protected IFuture<Void> testComponentResult(final String config, final String expected, final IInternalAccess ia)
	{
		final Future<Void>	fut	= new Future<Void>();

		ia.createComponent(new CreationInfo(config, null, ia.getId()).setFilename("jadex/micro/testcases/Result.component.xml"), null)
			.addResultListener(ia.getFeature(IExecutionFeature.class).createResultListener(new ExceptionDelegationResultListener<IExternalAccess, Void>(fut)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				ia.getExternalAccess(result.getId()).killComponent()
					.addResultListener(new ExceptionDelegationResultListener<Map<String, Object>, Void>(fut)
				{
					public void customResultAvailable(Map<String, Object> results)
					{
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
