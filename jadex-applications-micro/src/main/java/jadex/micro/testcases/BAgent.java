package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IBService.class, implementation=@Implementation(expression="$component")))
//@Results(@Result(name="exception", typename="Exception"))
@Results(@Result(name="testcases", clazz=List.class))
public class BAgent extends MicroAgent implements IBService
{
	@ServiceComponent
	protected IInternalAccess access;
	
	/**
	 *  Init service method.
	 */
	@ServiceStart
	public IFuture start()
	{
		final List tests = new ArrayList();

		final Future ret = new Future();
		SServiceProvider.getService(access.getServiceContainer(), IAService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(access.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println("found service");
				IAService ser = (IAService)result;
				tests.add(new TestReport("#B1", "Test if service could be found in init.", true, null));

				ser.test().addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						String reason = getComponentAdapter().isExternalThread()? "Wrong thread: "+Thread.currentThread(): null;
						tests.add(new TestReport("#B2", "Test if comes back on component thread.", !getComponentAdapter().isExternalThread(), reason));
						setResultValue("testcases", tests);
//						System.out.println("invoked service");
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						String reason = getComponentAdapter().isExternalThread()? "Wrong thread: "+Thread.currentThread(): null;
						tests.add(new TestReport("#B2", "Test if comes back on component thread.", !getComponentAdapter().isExternalThread(), reason));
						setResultValue("testcases", tests);
						ret.setResult(null);
					}
				});
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tests.add(new TestReport("#B1", "Test if service could be found in init.", false, exception.getMessage()));
				setResultValue("testcases", tests);
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
		
}