package jadex.bdi.planlib.simsupport.environment.field;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

public interface IObjectField extends IField
{
	/** Returns the object at a specific position.
	 *  
	 *  @param position
	 *  @return object
	 */
	public IVector1 getScalar(IVector2 position);
}
