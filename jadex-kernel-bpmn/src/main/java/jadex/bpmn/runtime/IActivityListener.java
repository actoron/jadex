package jadex.bpmn.runtime;

import jadex.commons.ChangeEvent;

public interface IActivityListener
{
	/**
	 *  Invoked when an activity is executed.
	 *  @param ce The event..
	 */
	public void activityExecuting(ChangeEvent ce);
}
