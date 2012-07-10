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

			double rangle = rot.getDirectionAsDouble();
			double tangle = targetdir.getDirectionAsDouble();
			if(Math.abs(rangle-tangle)>0.1)
			{
				double f = rangle>tangle? -1: 1;
				double d = Math.abs(rangle-tangle);
				rangle = d<Math.PI? rangle+0.1*f: rangle-0.1*f;
				
				double x = Math.cos(rangle);
				double y = Math.sin(rangle);
				obj.setProperty(PROPERTY_ROTATION, new Vector2Double(x,y));
			}
			else
			{
				setFinished(space, obj, true);
			}
		}
	}
	
	/**
	 * 
	 */
	public static void main(String[] args)
	{
		IVector2 rot = new Vector2Double(0,0);
		IVector2 targetdir = new Vector2Double(10,10);

		while(true)
		{
			double rangle = rot.getDirectionAsDouble();
			double tangle = targetdir.getDirectionAsDouble();
			
			System.out.println("dir: "+Math.toDegrees(rangle)+" "+Math.toDegrees(tangle));
			
			if(Math.abs(rangle-tangle)>0.05)
			{
				double f = rangle>tangle? -1: +1;
				double d = Math.abs(rangle-tangle);
				rangle = d<Math.PI? rangle+0.1*f: rangle-0.1*f;
				
				double x = Math.cos(rangle);
				double y = Math.sin(rangle);
				
				rot = new Vector2Double(x,y);
			}
		}
	}
}
