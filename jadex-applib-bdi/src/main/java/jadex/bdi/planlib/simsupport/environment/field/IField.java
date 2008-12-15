package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;

/** Interface for (physical) fields. Some methods are optional and
 *  implementation-dependent.
 */
public interface IField
{
	/** Initializes the field.
	 *  
	 *  @param engine the simulation engine the field is on
	 */
	public void init(ISimulationEngine engine);
	
	/** Updates the field.
	 *  
	 *  @param deltaT time passed during this simulation step
	 *  @param engine the simulation engine the field is on
	 */
	public void update(IVector1 deltaT, ISimulationEngine engine);
	
	// TODO: Move to appropriate interfaces
	/** Returns the scalar field value at a specific position.
	 *  This represents a scalar field view.
	 *  This method is optional.
	 *  
	 *  @param position
	 *  @return scalar value
	 *  @throws UnsupportedOperationException if the operation is unsupported.
	 */
	//public IVector1 getScalar(IVector2 position);
	
	/** Returns the vector field value at a specific position.
	 *  The returned value may be a gradient of an underlying scalar field
	 *  or the local value of a specific vector field.
	 *  This method is optional.
	 *  
	 * 	@param position
	 * 	@return vector value
	 * 	@throws UnsupportedOperationException if the operation is unsupported.
	 */
	//public IVector2 getVector(IVector2 position);
}
