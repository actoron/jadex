package jadex.wfms.service.listeners;

import jadex.commons.IRemotable;
import jadex.commons.future.IFuture;

public interface IProcessListener extends IRemotable
{
	/**
	 * This method is invoked when a process finishes.
	 * @param event the finished process event
	 */
	public IFuture processFinished(ProcessEvent event);
}
