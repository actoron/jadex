package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector2;


public interface IVectorField extends IField
{
	/**
	 * Returns the vector field value at a specific position. The returned value
	 * may be a gradient of an underlying scalar field or the local value of a
	 * specific vector field.
	 * 
	 * @param position
	 * @return vector value
	 */
	public IVector2 getVector(IVector2 position);
}
