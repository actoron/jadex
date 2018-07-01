package jadex.launch.test.remotereference.servicecallback;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service to be passed as argument.
 */
@Service
public interface ICalledService
{
	/**
	 *  "Reply" from test service.
	 */
	public IFuture<Void> call();
}
