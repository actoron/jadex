package jadex.bdi.planlib.simsupport.simcap;

import jadex.bdi.planlib.simsupport.environment.ISimObjectStateListener;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IInternalEvent;

/** A local ISimObjectStateListener.
 */
public class LocalSimObjectStateListener implements ISimObjectStateListener
{
	/** Access to the listening agent.
	 */
	private IExternalAccess agent_;
	
	/** Creates a new LocalSimObjectStateListener.
	 * 
	 * @param agent external access for triggering agent events.
	 */
	public LocalSimObjectStateListener(IExternalAccess agent)
	{
		agent_ = agent;
	}
	
	public void destinationReached(SimulationEvent event)
	{
		IInternalEvent destinationReachedEvent = 
			agent_.createInternalEvent(ISimObjectStateListener.DESTINATION_REACHED);
		destinationReachedEvent.getParameter("object_id").setValue(event.getParameter("object_id"));
		agent_.dispatchInternalEvent(destinationReachedEvent);
	}
}
