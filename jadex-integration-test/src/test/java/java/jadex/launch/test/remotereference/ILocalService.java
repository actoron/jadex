package jadex.launch.test.remotereference;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

/**
 *  Service to be found on the local platform.
 */
public interface ILocalService
{
	public IFuture<Void> executeCallback(@Reference ICallback callback);
	
	public IFuture<Void> executeCallback(@Reference ICallbackReference callback);
}
