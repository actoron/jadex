package jadex.wfms.service;

import jadex.bridge.IComponentChangeEvent;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.wfms.service.listeners.ILogListener;

public interface ILogService
{
	/**
	 *  Adds a log listener.
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @param pastEvents True, if past events should be passed to the listener.
	 *  @return Indication of success.
	 */
	public IFuture<Void> addLogListener(IComponentIdentifier client, ILogListener listener, boolean pastEvents);
	
	/**
	 *  Removes a log listener.
	 *  
	 *  @param client The client adding the listener or null for none.
	 *  @param listener The listener.
	 *  @return Indication of success.
	 */
	public IFuture<Void> removeLogListener(IComponentIdentifier client, ILogListener listener);
	
	/**
	 *  Writes an event into the WfMS log.
	 *  @param event The event.
	 *  @return Null, when done.
	 */
	public IFuture<Void> logEvent(IComponentChangeEvent event);
}
