package jadex.bdi.planlib.simsupport.environment.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.environment.simobject.SimObject;

public interface ISimObjectTask
{
	public void executeTask(IVector1 deltaT, SimObject object);
}
