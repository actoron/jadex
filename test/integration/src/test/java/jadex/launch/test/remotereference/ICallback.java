package jadex.launch.test.remotereference;

import jadex.commons.future.IFuture;

//@Reference
public interface ICallback
{
	public IFuture<Void> call();
}
