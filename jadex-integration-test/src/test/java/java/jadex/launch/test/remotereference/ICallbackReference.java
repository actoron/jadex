package jadex.launch.test.remotereference;

import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

@Reference
public interface ICallbackReference
{
	public IFuture<Void> call();
}
