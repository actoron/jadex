package jadex.commons;

import jadex.commons.future.IFuture;


/**
 *  Remote version of the change listener.
 */
// @Reference
public interface IRemoteChangeListener<T> extends IRemotable
{
	/**
	 *  Called when a change occurs.
	 *  Signature has a return value for understanding when an exception 
	 *  occurs so that there is a chance to remove the listener:
	 *  @param event The event.
	 */
	public IFuture<Void> changeOccurred(ChangeEvent<T> event);
}
