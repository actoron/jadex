package jadex.android.clientapp.bditest;

import jadex.commons.future.IFuture;

public interface IDisplayTextService
{
	public IFuture<Void>	showUiMessage(String message);
}
