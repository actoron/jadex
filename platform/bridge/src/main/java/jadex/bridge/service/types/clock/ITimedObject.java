package jadex.bridge.service.types.clock;

/**
 *  Interface for objects that are interested in timepoints.
 */
public interface ITimedObject
{
	/**
	 *  Called when the submitted timepoint was reached.
	 *  // todo: will be enhanced with a TimerEvent when
	 *  // we enhance the time service 
	 */
	public void timeEventOccurred(long currenttime);
}
