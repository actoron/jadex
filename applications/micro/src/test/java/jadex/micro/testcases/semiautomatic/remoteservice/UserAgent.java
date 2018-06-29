package jadex.micro.testcases.semiautomatic.remoteservice;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.remote.IRemoteServiceManagementService;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that invokes methods on a remote service.
 */
@Agent
public class UserAgent
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		final CounterResultListener<Void> lis = new CounterResultListener<Void>(2, new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				ret.setResult(null);
//				killAgent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(null);
//				killAgent();
			}
		});
		
		// get remote management service 
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new IResultListener<IComponentManagementService>()
		{
			public void resultAvailable(final IComponentManagementService cms)
			{
				// get remote management service and fetch service via rms.getProxy()
				agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IRemoteServiceManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new IResultListener<IRemoteServiceManagementService>()
				{
					public void resultAvailable(IRemoteServiceManagementService rms)
					{
						IComponentIdentifier platid = new ComponentIdentifier("remote", 
							new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});

						// Search for remote service
						IFuture<IMathService> fut = rms.getServiceProxy(agent.getComponentIdentifier(), platid, new ClassInfo(IMathService.class), RequiredServiceInfo.SCOPE_PLATFORM, null);
						fut.addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<IMathService>()
						{
							public void resultAvailable(IMathService service)
							{
								invokeAddService("IMathService searched via rms.", service)
									.addResultListener(lis);
							}
							public void exceptionOccurred(Exception exception)
							{
								exception.printStackTrace();
								lis.resultAvailable(null);
							}
						}));
					}						
					public void exceptionOccurred(Exception exception)
					{
						lis.resultAvailable(null);
					}
				});
			}
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		// search on local platform and find service via ProxyAgent to other platform
		agent.getComponentFeature(IRequiredServicesFeature.class).searchService(IMathService.class, RequiredServiceInfo.SCOPE_GLOBAL)
			.addResultListener(new IResultListener<IMathService>()
		{
			public void resultAvailable(IMathService service)
			{
				invokeAddService("IMathService searched via platform proxy.", service)
					.addResultListener(lis);
			}
			public void exceptionOccurred(Exception exception)
			{
				lis.resultAvailable(null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Invoke some add methods for testing.
	 */
	protected IFuture<Void> invokeAddService(String info, IMathService service)
	{
		final Future<Void> ret = new Future<Void>();
		
		if(service==null)
		{
			System.out.println("No remote add service found: "+info);
			ret.setResult(null);
		}
		else
		{
			final CounterResultListener<Void> lis = new CounterResultListener<Void>(2, new DelegationResultListener<Void>(ret));
			System.out.println("Found service: "+info);
			// Execute non-blocking method call with future result
//			System.out.println("Calling non-blocking addNB method.");
			service.addNB(1, 2).addResultListener(new IResultListener<Integer>()
			{
				public void resultAvailable(Integer result)
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
			service.divZero().addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
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
