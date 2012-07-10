package jadex.bdi.examples.marsworld.movement;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.Iterator;
import java.util.Set;

/**
 * 
 */
public class RotationTask extends AbstractTask
{
	/** The destination property. */
	public static final String	PROPERTY_TYPENAME = "rotate";
	
	/** .*/
	public static final String	PROPERTY_ROTATION	= "rotation";

	
	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		IVector2 destination = (IVector2)getProperty(MoveTask.PROPERTY_DESTINATION);
		IVector2 loc = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		
		IVector2 rot = (IVector2)obj.getProperty(PROPERTY_ROTATION);
		
		if(rot==null)
		{
			obj.setProperty(PROPERTY_ROTATION, destination.copy().subtract(loc).normalize());
			setFinished(space, obj, true);
		}
		else
		{
			IVector2 targetdir = destination.copy().subtract(loc).normalize();
			IVector2 diff = targetdir.copy().subtract(rot);
			double rx = rot.getXAsDouble();
			double ry = rot.getYAsDouble();
			double dx = diff.getX().getAsDouble();
			double dy = diff.getY().getAsDouble();
			double d = 0.05;
			double nrx = dx>0? rx+d: dx<0? rx-d: rx;
			double nry = dy>0? ry+d: dy<0? ry-d: ry;
			obj.setProperty(PROPERTY_ROTATION, new Vector2Double(nrx, nry).normalize());
			
			if(dx<=0.05 && dy<=0.05)
				setFinished(space, obj, true);
		}
	}
}
