package jadex.bdi.planlib.simsupport.environment;

public interface ISimObjectStateListener
{
	// Event Types
	public static final String DESTINATION_REACHED = "simobj_dest_reached";
	
	/** This event gets called when an object reached its destination.
	 */
	public void destinationReached(SimulationEvent event);
}
