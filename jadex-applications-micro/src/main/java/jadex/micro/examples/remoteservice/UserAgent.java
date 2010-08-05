package jadex.micro.examples.remoteservice;

import jadex.bridge.IComponentManagementService;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.service.SServiceProvider;

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
		// get remote management service 
		SServiceProvider.getServiceUpwards(getServiceProvider(), IComponentManagementService.class)
			.addResultListener(createResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
//				final IComponentManagementService cms = (IComponentManagementService)result;
				
				// get remote management service and fetch service via rms.getProxy()
				/*SServiceProvider.getService(getServiceProvider(), IRemoteServiceManagementService.class)
					.addResultListener(createResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IRemoteServiceManagementService rms = (IRemoteServiceManagementService)result;

//							IComponentIdentifier rrms = cms.createComponentIdentifier("rms@remote", false, 
//								new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});
						IComponentIdentifier platid = cms.createComponentIdentifier("root@remote", false, 
							new String[]{"tcp-mtp://127.0.0.1:11000", "nio-mtp://127.0.0.1:11001"});

						// Search for remote service
//							rms.getProxy(rrms, null, IAddService.class).addResultListener(createResultListener(new DefaultResultListener()
						rms.getServiceProxy(platid, null, IAddService.class).addResultListener(createResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								IAddService service = (IAddService)((ServiceInfo)result).getService();
								invokeAddService("IAddService searched via rms.", service);
							}
						}));
					}						
				}));*/
				
				// search on local platform and find service via ProxyAgent to other platform
				SServiceProvider.getService(getServiceProvider(), IAddService.class, false)
					.addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						IAddService service = (IAddService)result;
						invokeAddService("IAddService searched via platform proxy.", service);
					}
				});
			}
		}));
	}
	
	/**
	 *  Invoke some add methods for testing.
	 */
	protected void invokeAddService(String info, IAddService service)
	{
		if(service==null)
		{
			System.out.println("No remote add service found: "+info);
		}
		else
		{
			// Execute non-blocking method call with future result
			System.out.println("Calling non-blocking addNB method.");
			service.addNB(1, 2).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					System.out.println("Invoked addNB: "+result);
				}
			});
			
			// Execute blocking method call with normal result
			System.out.println("Calling blocking addB method.");
			int res= service.addB(1, 2);
			System.out.println("Invoked addB: "+res);
		}
	}
}
