package jadex.android.clientappdemo.agent;

import jadex.commons.future.IFuture;

public interface IDisplayTextService
{
	public IFuture<Void>	showUiMessage(String message);
}
