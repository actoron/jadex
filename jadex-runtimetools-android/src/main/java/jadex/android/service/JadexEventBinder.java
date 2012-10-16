package jadex.android.service;

import android.os.Binder;
import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.service.types.context.IJadexAndroidEvent;

public abstract class JadexEventBinder extends Binder
{
	public void registerEventListener(String eventName, IEventReceiver<?> rec)
	{
		AndroidContextManager.getInstance().registerEventListener(eventName, rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassException
	{
		return AndroidContextManager.getInstance().dispatchEvent(event);
	}

	public boolean unregisterEventListener(String eventName, IEventReceiver<?> rec)
	{
		return AndroidContextManager.getInstance().unregisterEventListener(eventName, rec);
	}
}
