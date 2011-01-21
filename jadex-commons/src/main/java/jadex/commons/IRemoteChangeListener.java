package jadex.commons;


/**
 *  Remote version of the change listener.
 */
public interface IRemoteChangeListener extends IRemotable
{
	/**
	 *  Called when a change occurs.
	 *  Signature has a return value for understanding when an exception 
	 *  occurs so that there is a chance to remove the listener:
	 *  @param event The event.
	 */
	public IFuture changeOccurred(ChangeEvent event);
}
