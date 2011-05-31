package jadex.micro.testcases;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@ProvidedServices(@ProvidedService(type=IBService.class, implementation=@Implementation(expression="$component")))
public class BAgent extends MicroAgent implements IBService
{
	@ServiceComponent
	protected IInternalAccess access;
	
	/**
	 * 
	 */
	@ServiceStart
	public IFuture start()
	{
		final Future ret = new Future();
		SServiceProvider.getService(access.getServiceContainer(), IAService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(access.createResultListener(new DelegationResultListener(ret)
		{
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("here");
				super.exceptionOccurred(exception);
			}
		}));
		
		return ret;
	}
}