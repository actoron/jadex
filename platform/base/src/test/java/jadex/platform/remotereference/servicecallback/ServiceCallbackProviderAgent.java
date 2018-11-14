package jadex.platform.remotereference.servicecallback;

import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;


/**
 *  Agent implementing the test service.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
public class ServiceCallbackProviderAgent implements ICallerService
{
	/**
	 *  Calls the given service.
	 */
	public IFuture<Void> doCall(ICalledService callback)
	{
		return callback.call();
	}
}
