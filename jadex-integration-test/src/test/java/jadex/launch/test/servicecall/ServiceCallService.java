package jadex.launch.test.servicecall;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.Timeout;
import jadex.commons.future.IFuture;

/**
 *  Implementation of a service.
 */
@Service
public class ServiceCallService	implements IServiceCallService
{
	/**
	 *  Dummy method for service call benchmark.
	 */
	public IFuture<Void> call()
	{
		return IFuture.DONE;
	}
}