package jadex.bdiv3.examples.marsworld.movement;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

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
		double	speed	= ((Number)obj.getProperty(MoveTask.PROPERTY_SPEED)).doubleValue();
		
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

			double	delta_rot	= 0.005;	// per millis, i.e. 0.001 = 1/speed seconds for half circle.
			double	delta_mov	= 0.0005;	// per millis, i.e. 0.001 = original speed, 0.0005 = half original speed
			
			double rangle = rot.getDirectionAsDouble();
			double tangle = targetdir.getDirectionAsDouble();
			if(Math.abs(rangle-tangle)>progress*delta_rot*speed)
			{
				double f = rangle>tangle? -1: 1;
				double d = Math.abs(rangle-tangle);
				rangle = d<Math.PI? rangle+progress*delta_rot*speed*f: rangle-progress*delta_rot*speed*f;
				
				double x = Math.cos(rangle);
				double y = Math.sin(rangle);
				IVector2 newdir = new Vector2Double(x,y);
				obj.setProperty(PROPERTY_ROTATION, newdir);
				
				double	maxdist	= delta_mov / delta_rot;
				double dist = ((Space2D)space).getDistance(loc, destination).getAsDouble();
				if(dist>maxdist /*|| Math.random()>0.7*/)
				{
					IVector2 newloc	= newdir.copy().normalize().multiply(progress*speed*delta_mov).add(loc);
					((Space2D)space).setPosition(obj.getId(), newloc);
				}
			}
			else
			{
				double x = Math.cos(tangle);
				double y = Math.sin(tangle);
				IVector2 newdir = new Vector2Double(x,y);
				obj.setProperty(PROPERTY_ROTATION, newdir);
				
				setFinished(space, obj, true);
			}
		}
		
		final IExternalAccess agent = (IExternalAccess)getProperty(MoveTask.PROPERTY_SCOPE);
		MoveTask.processVision(space, obj, agent);
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
