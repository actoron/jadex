package jadex.micro.testcases;

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

import javax.management.ServiceNotFoundException;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=IBService.class, implementation=@Implementation(expression="$component")))
@Results(@Result(name="exception", typename="Exception"))
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
		final Future ret = new Future();
		SServiceProvider.getService(access.getServiceContainer(), IAService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(access.createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
//				System.out.println("found service");
				IAService ser = (IAService)result;
				ser.test().addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
//						System.out.println("invoked service");
						ret.setResult(result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						setResultValue("exception", new RuntimeException("Service invocation exception"));
						ret.setResult(null);
					}
				}));
			}
			
			public void exceptionOccurred(Exception exception)
			{
				setResultValue("exception", new ServiceNotFoundException("IAService"));
				ret.setResult(null);
			}
		}));
		
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	@ServiceStart
//	public IFuture start()
//	{
//		final Future ret = new Future();
//		SServiceProvider.getService(access.getServiceContainer(), IAService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(access.createResultListener(new DelegationResultListener(ret)
//		{
//			public void customResultAvailable(Object result)
//			{
//				System.out.println("found service");
//				IAService ser = (IAService)result;
//				ser.test().addResultListener(createResultListener(new DelegationResultListener(ret)
//				{
//					public void customResultAvailable(Object result) 
//					{
//						System.out.println("invoked service");
//						ret.setResult(result);
////						ret.setException(new RuntimeException());
//					};
//				}));
//			}	
//				
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("could not find service");
//				super.exceptionOccurred(exception);
//			}
//		}));
//		
//		return ret;
//	}
		
}