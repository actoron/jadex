package jadex.android;

public interface EventReceiver<T extends JadexAndroidEvent> {
	void receiveEvent(T event);
	
	Class getEventClass();
}
