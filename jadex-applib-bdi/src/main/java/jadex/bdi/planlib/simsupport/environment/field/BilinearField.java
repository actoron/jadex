package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.ISimulationEngine;


/**
 * Discrete scalar field with gradient support using a simple, bilinear
 * interpolation.
 */
public class BilinearField implements IField, IScalarField, IVectorField
{
	/**
	 * Initializes the field.
	 * 
	 * @param engine the simulation engine the field is on
	 */
	public void init(ISimulationEngine engine)
	{
	}

	/**
	 * Updates the field.
	 * 
	 * @param deltaT time passed during this simulation step
	 * @param engine the simulation engine the field is on
	 */
	public void update(IVector1 deltaT, ISimulationEngine engine)
	{
	}

	/**
	 * Returns the scalar field value at a specific position.
	 * 
	 * @param position
	 * @return scalar value
	 */
	public IVector1 getScalar(IVector2 position)
	{
		return null;
	}

	/**
	 * Returns the vector field value at a specific position. The returned value
	 * may be a gradient of an underlying scalar field or the local value of a
	 * specific vector field.
	 * 
	 * @param position
	 * @return vector value
	 */
	public IVector2 getVector(IVector2 position)
	{
		return null;
	}

	public void addAtPosition(IVector2 position, IVector1 value)
	{

	}
}
