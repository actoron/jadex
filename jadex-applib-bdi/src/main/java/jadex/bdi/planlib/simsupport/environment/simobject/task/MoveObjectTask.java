package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

/** Tasks that moves the object according to its velocity.
 */
public class MoveObjectTask implements ISimObjectTask
{
	public void executeTask(IVector1 deltaT, SimObject object)
	{
		IVector2 pDelta = object.getVelocity().copy().multiply(deltaT);
		object.getPosition().add(pDelta);
	}
	
	public boolean equals(Object obj)
	{
		if (obj instanceof MoveObjectTask)
		{
			return true;
		}
		return false;
	}
}
