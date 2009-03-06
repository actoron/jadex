/**
 * 
 */
package jadex.bdi.planlib.simsupport.environment.grid.simobject.task;

import jadex.bdi.planlib.simsupport.common.math.IVector1;
import jadex.bdi.planlib.simsupport.common.math.IVector2;

/**
 * @author claas
 *
 */
public class GoRightTask extends GoToDirectionTask
{

	/**
	 * @param speed
	 * @param areaSize
	 * @param area_behavior
	 */
	public GoRightTask(IVector1 speed, IVector2 areaSize, int area_behavior)
	{
		super(GoToDirectionTask.DEFAULT_NAME, GoToDirectionTask.DIRECTION_RIGHT, speed, areaSize, area_behavior);
	}

}
