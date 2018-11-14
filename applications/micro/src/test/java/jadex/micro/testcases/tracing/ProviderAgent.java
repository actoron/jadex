package jadex.micro.testcases.tracing;

import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;


/**
 * 
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class ProviderAgent implements ITestService
{
	@Agent
	protected IInternalAccess agent;
	
//	@AgentCreated
//	public void created()
//	{
//		agent.getLogger().severe("Agent created: "+agent.getDescription());
//	}

	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method(final String msg)
	{
		final Future<Void> ret = new Future<Void>();
		
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		System.out.println("Called method1: "+msg+" "+sc+" "+Thread.currentThread());
		
		agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ITestService.class, ServiceScope.COMPONENT_ONLY))
			.addResultListener(new ExceptionDelegationResultListener<ITestService, Void>(ret)
		{
			public void customResultAvailable(ITestService ts)
			{
				System.out.println("Called method1 after search: "+ServiceCall.getCurrentInvocation()+" "+Thread.currentThread());
				
				ts.method2(msg).addResultListener(new DelegationResultListener<Void>(ret)
				{
					public void customResultAvailable(Void result)
					{
						ServiceCall sc = ServiceCall.getCurrentInvocation();
						System.out.println("Called service: "+msg+" "+sc+" "+Thread.currentThread());
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	/**
	 *  Call a method that must use a secure
	 *  transport under the hood.
	 */
	public IFuture<Void> method2(String msg)
	{
		ServiceCall sc = ServiceCall.getCurrentInvocation();
		System.out.println("Called method2: "+msg+" "+sc+" "+Thread.currentThread());
		return IFuture.DONE;
	}
}
