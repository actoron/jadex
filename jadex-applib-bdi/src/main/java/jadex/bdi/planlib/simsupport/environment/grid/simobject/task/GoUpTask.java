/**
 * 
 */
package jadex.bdi.planlib.simsupport.environment.grid.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.environment.grid.simobject.task.GoToDirectionTask;

/**
 * @author claas
 *
 */
public class GoUpTask extends GoToDirectionTask
{

	/**
	 * @param speed
	 * @param areaSize
	 * @param area_behavior
	 */
	public GoUpTask(IVector1 speed, IVector2 areaSize, int area_behavior)
	{
		super(GoToDirectionTask.DEFAULT_NAME, GoToDirectionTask.DIRECTION_UP, speed, areaSize, area_behavior);
	}

}
