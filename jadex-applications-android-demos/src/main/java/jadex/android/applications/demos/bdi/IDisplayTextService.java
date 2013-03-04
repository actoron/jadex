package jadex.android.applications.demos.bdi;

import jadex.commons.future.IFuture;

public interface IDisplayTextService
{
	public IFuture<Void>	showUiMessage(String message);
}
