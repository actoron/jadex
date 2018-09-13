package jadex.platform.remotereference;

import jadex.commons.future.IFuture;

//@Reference
public interface ICallback
{
	public IFuture<Void> call();
}
