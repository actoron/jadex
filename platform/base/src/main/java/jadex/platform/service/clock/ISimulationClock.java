package jadex.platform.service.clock;

import jadex.bridge.service.types.clock.IClock;



/**
 *  Simulation clock interface extends a normal clock
 *  by adding a method for advancing the method time.
 */
public interface ISimulationClock extends IClock
{	
	/**
	 *  Advance one event.
	 *  @return True, if clock could be advanced.
	 */
	public boolean advanceEvent();
}
