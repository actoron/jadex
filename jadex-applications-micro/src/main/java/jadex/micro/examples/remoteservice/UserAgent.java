package jadex.micro.examples.remoteservice;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IRemoteServiceManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.micro.MicroAgent;

/**
 *  Agent that invokes methods on a remote service.
 */
public class UserAgent extends MicroAgent
{
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public void executeBody()
	{
		final CounterResultListener lis = new CounterResultListener(2, new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				killAgent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				killAgent();
			}
		});
		
		// get remote management service 
		SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				
				// get remote management service and fetch service via rms.getProxy()
				SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
					.addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;

						IComponentIdentifier platid = cms.createComponentIdentifier("remote", false, 
							new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});

						// Search for remote service
						rms.getServiceProxy(platid, IMathService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(createResultListener(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								IMathService service = (IMathService)result;
								invokeAddService("IMathService searched via rms.", service)
									.addResultListener(createResultListener(lis));
							}
							public void exceptionOccurred(Exception exception)
							{
								lis.resultAvailable(null);
							}
						}));
					}						
					public void exceptionOccurred(Exception exception)
					{
						lis.resultAvailable(null);
					}
				}));
			}
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		}));
		
		// search on local platform and find service via ProxyAgent to other platform
		SServiceProvider.getService(getServiceProvider(), IMathService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				IMathService service = (IMathService)result;
				invokeAddService("IMathService searched via platform proxy.", service)
					.addResultListener(createResultListener(lis));
			}
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		}));
	}
	
	/**
	 *  Invoke some add methods for testing.
	 */
	protected IFuture invokeAddService(String info, IMathService service)
	{
		final Future ret = new Future();
		
		if(service==null)
		{
			System.out.println("No remote add service found: "+info);
			ret.setResult(null);
		}
		else
		{
			final CounterResultListener lis = new CounterResultListener(2, new DelegationResultListener(ret));
			System.out.println("Found service: "+info);
			// Execute non-blocking method call with future result
//			System.out.println("Calling non-blocking addNB method.");
			service.addNB(1, 2).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					System.out.println("Invoked addNB: "+result);
					lis.resultAvailable(null);
				}
				public void exceptionOccurred(Exception exception)
				{
					lis.resultAvailable(null);
				}
			});
			
			// Execute blocking method call with normal result
//			System.out.println("Calling blocking addB method.");
			int res = service.addB(1, 2);
			System.out.println("Invoked addB: "+res);
			
			// Execute constant method call, which does not block but returns a cached value.
			System.out.println("Calling constant (non-blocking) getPi method.");
			double pi = service.getPi();
			System.out.println("Invoked getPi: "+pi);
			
			System.out.println("Calling void (non-blocking) printMessage method.");
			service.printMessage("math service");
			System.out.println("Invoked printMessage");
			
			System.out.println("Calling (non-blocking) exception throwing divZero method.");
			service.divZero().addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					System.out.println("Invoked divZero without exception");
					lis.resultAvailable(null);
				}
				
				public void exceptionOccurred(Exception exception)
				{
					System.out.println("Invoked divZero, expected exception occurred: "+exception);
					lis.resultAvailable(null);
//					exception.printStackTrace();
				}
			});
		}
		
		return ret;
	}
}
