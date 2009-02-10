package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;


/**
 * Interface for general fields.
 */
public interface IField
{
	/**
	 * Initializes the field.
	 * 
	 * @param engine the simulation engine the field is on
	 */
	public void init(ISimulationEngine engine);

	/**
	 * Updates the field.
	 * 
	 * @param deltaT time passed during this simulation step
	 * @param engine the simulation engine the field is on
	 */
	public void update(IVector1 deltaT, ISimulationEngine engine);
}
