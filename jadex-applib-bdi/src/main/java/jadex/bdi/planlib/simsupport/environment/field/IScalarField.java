package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

public interface IScalarField extends IField
{
	/** Returns the scalar field value at a specific position.
	 *  
	 *  @param position
	 *  @return scalar value
	 */
	public IVector1 getScalar(IVector2 position);
}
