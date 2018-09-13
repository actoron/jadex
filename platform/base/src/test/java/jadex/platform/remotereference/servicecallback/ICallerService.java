package jadex.platform.remotereference.servicecallback;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service that receives another service as argument.
 */
@Service
public interface ICallerService
{
	/**
	 *  Calls the given service.
	 */
	public IFuture<Void> doCall(ICalledService callback);
}
