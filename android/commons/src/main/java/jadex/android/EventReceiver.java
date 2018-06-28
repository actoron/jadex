package jadex.android;

import jadex.bridge.service.types.context.IJadexAndroidEvent;


public abstract class EventReceiver<T extends IJadexAndroidEvent> implements IEventReceiver<T> {

	private Class<T> eventClass;

	public EventReceiver(Class<T> eventClass)
	{
		this.eventClass = eventClass;
	}
	
	@Override
	public String getType()
	{
		return eventClass.getCanonicalName();
	}

	@Override
	public Class<T> getEventClass()
	{
		return eventClass;
	}

}
