package jadex.android.clientappdemo.bdiagent;

import jadex.commons.future.IFuture;

public interface IDisplayTextService
{
	public IFuture<Void>	showUiMessage(String message);
}
