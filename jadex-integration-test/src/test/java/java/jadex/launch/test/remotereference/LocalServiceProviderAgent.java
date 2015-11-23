package jadex.launch.test.remotereference;

import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing the local service.
 */
@Agent
@Imports("jadex.micro.*")
@ProvidedServices(@ProvidedService(type=ILocalService.class, implementation=@Implementation(expression="$pojoagent")))
@Service
public class LocalServiceProviderAgent implements ILocalService
{

	@Override
	public IFuture<Void> executeCallback(@Reference ICallback callback)
	{
		Future<Void> ret = new Future<Void>();
		System.out.println("calling back...");
		IFuture<Void> call = callback.call();
		call.addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}

	@Override
	public IFuture<Void> executeCallback(ICallbackReference callback)
	{
		Future<Void> ret = new Future<Void>();
		System.out.println("calling back...");
		IFuture<Void> call = callback.call();
		call.addResultListener(new DelegationResultListener<Void>(ret));
		return ret;
	}

}
