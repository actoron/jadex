package jadex.commons;

/**
 *  Listener for being notified on change events.
 */
// @Reference
public interface IChangeListener
{
	/**
	 *  Called when a change occurs.
	 *  @param event The event.
	 */
	public void changeOccurred(ChangeEvent event);
}
