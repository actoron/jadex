package jadex.wfms.service.listeners;

import jadex.bridge.IComponentChangeEvent;
import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

public interface ILogListener extends IRemotable
{
	/**
	 * This method is invoked on new log messages.
	 * @param message the new log message
	 */
	public IFuture logMessage(IComponentChangeEvent event);
}
