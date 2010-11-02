package jadex.tools.daemon;

import jadex.commons.ChangeEvent;
import jadex.commons.IFuture;
import jadex.commons.IRemotable;

/**
 * 
 */
public interface IRemoteChangeListener extends IRemotable//, IChangeListener, 
{
	/**
	 *  Called when a change occurs.
	 *  @param event The event.
	 */
	public IFuture changeOccurred(ChangeEvent event);
}
