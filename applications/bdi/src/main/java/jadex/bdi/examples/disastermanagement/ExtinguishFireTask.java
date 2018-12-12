package jadex.bdi.examples.disastermanagement;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Extinguish fire at a disaster.
 */
public class ExtinguishFireTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "extinguish_fire";
	
	/** The disaster property. */
	public static final String	PROPERTY_DISASTER = "disaster";
	
	/** The extinguished property (of the space object). */
	public static final String	PROPERTY_EXTINGUISHED = "extinguished";

	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		// Check if brigade object is in range of fire.
		Space2D	space2d	= (Space2D)space;
		ISpaceObject	disaster	= (ISpaceObject)getProperty(PROPERTY_DISASTER);
		double	range	= ((Number)disaster.getProperty("size")).intValue()/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		if(space2d.getDistance((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION),
			(IVector2)disaster.getProperty(Space2D.PROPERTY_POSITION)).getAsDouble()>range*1.1) // allow for 10% rounding error
		{
			throw new RuntimeException("Fire out of range: "+obj);
		}

		// Update disaster object based on time progress.
		int	cnt	= 0;
		double	extinguished	= ((Number)obj.getProperty(PROPERTY_EXTINGUISHED)).doubleValue();
		extinguished	+= progress*0.0002;	// 1 fire per 5 seconds.
		while(extinguished>1)
		{
			cnt++;
			extinguished	-= 1;
		}
		int fire	= ((Number)disaster.getProperty("fire")).intValue();
		fire	= Math.max(fire-cnt, 0);
		disaster.setProperty("fire", Integer.valueOf(fire));
		
		// Remove disaster object when everything is now fine.
		if(fire==0 && ((Number)disaster.getProperty("chemicals")).intValue()==0 && ((Number)disaster.getProperty("victims")).intValue()==0)
		{
			if(space2d.getSpaceObject0(disaster.getId())!=null)
				space.destroySpaceObject(disaster.getId());
		}

		// If not finished but least one fire was extinguished
		// use random to determine if need to move to another position for next fire.
		if(fire==0 || cnt>0 && Math.random()>0.5)
		{
			obj.setProperty(PROPERTY_EXTINGUISHED, Double.valueOf(0));
			setFinished(space, obj, true);
		}
		else
		{
			obj.setProperty(PROPERTY_EXTINGUISHED, Double.valueOf(extinguished));			
		}
	}
}
