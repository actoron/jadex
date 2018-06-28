package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IBService.class, implementation=@Implementation(expression="$pojoagent")))
//@Results(@Result(name="exception", typename="Exception"))
@Results(@Result(name="testcases", clazz=List.class))
@Service(IBService.class)
@Agent
public class BAgent implements IBService
{
	@ServiceComponent
	protected IInternalAccess agent;
	
	/**
	 *  Init service method.
	 */
	@ServiceStart
	public IFuture<Void> start()
	{
		final List<TestReport> tests = new ArrayList<TestReport>();

		final Future<Void> ret = new Future<Void>();
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IAService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IAService>()
		{
			public void resultAvailable(IAService ser)
			{
//				System.out.println("found service");
//				final IAService ser = (IAService)result;
				boolean ext = !agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
				String reason = ext? "Wrong thread: "+Thread.currentThread(): null;
				tests.add(new TestReport("#B1", "Test if service could be found in init.", !ext, reason));

				ser.test().addResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
						boolean ext = !agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
						String reason = ext? "Wrong thread: "+Thread.currentThread(): null;
						tests.add(new TestReport("#B2", "Test if comes back on component thread.", !ext, reason));
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testcases", tests);
//						System.out.println("invoked service: "+ser);
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						boolean ext = !agent.getComponentFeature(IExecutionFeature.class).isComponentThread();
						String reason = ext? "Wrong thread: "+Thread.currentThread(): null;
						tests.add(new TestReport("#B2", "Test if comes back on component thread.", !ext, reason));
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testcases", tests);
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tests.add(new TestReport("#B1", "Test if service could be found in init.", exception));
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testcases", tests);
				ret.setResult(null);
			}
		});
		
		return ret;
	}
		
}