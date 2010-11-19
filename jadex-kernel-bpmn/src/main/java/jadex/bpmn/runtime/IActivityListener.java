package jadex.bpmn.runtime;

import jadex.commons.ChangeEvent;

/**
 *  Listener that is called when activities are executed.
 */
public interface IActivityListener
{
	/**
	 *  Invoked when an activity is executed.
	 *  @param ce The event.
	 */
	public void activityStarted(ChangeEvent ce);
	
	/**
	 *  Invoked when an activity is executed.
	 *  @param ce The event.
	 */
	public void activityEnded(ChangeEvent ce);
}
