package jadex.bdi.planlib.simsupport.environment;

public interface ISimulationEventListener
{
	/**
	 * This event gets called when an simulation event is triggered.
	 */
	public void simulationEvent(SimulationEvent event);
}
