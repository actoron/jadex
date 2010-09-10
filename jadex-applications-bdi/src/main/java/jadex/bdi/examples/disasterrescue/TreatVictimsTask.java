package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.commons.service.clock.IClockService;

/**
 *  Extinguish fire at a disaster.
 */
public class TreatVictimsTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "treat_victims";
	
	/** The disaster property. */
	public static final String	PROPERTY_DISASTER = "disaster";
	
	/** The treated property (of the space object). */
	public static final String	PROPERTY_TREATED = "treated";

	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		// Check if ambulance object is in range of victims.
		Space2D	space2d	= (Space2D)space;
		ISpaceObject	disaster	= (ISpaceObject)getProperty(PROPERTY_DISASTER);
		double	range	= ((Number)disaster.getProperty("size")).intValue()/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		if(space2d.getDistance((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION),
			(IVector2)disaster.getProperty(Space2D.PROPERTY_POSITION)).getAsDouble()>range*1.1) // allow for 10% rounding error
		{
			throw new RuntimeException("Victims out of range: "+obj);
		}

		// Update disaster object based on time progress.
		int	cnt	= 0;
		double	treated	= ((Number)obj.getProperty(PROPERTY_TREATED)).doubleValue();
		treated	+= progress*0.0002;	// 1 victim per 5 seconds.
		while(treated>1)
		{
			cnt++;
			treated	-= 1;
		}
		int victims	= ((Number)disaster.getProperty("victims")).intValue();
		victims	= Math.max(victims-cnt, 0);
		disaster.setProperty("victims", new Integer(victims));

		// Remove disaster object when everything is now fine.
		if(victims==0 && ((Number)disaster.getProperty("fire")).intValue()==0 && ((Number)disaster.getProperty("chemicals")).intValue()==0)
		{
			if(space2d.getSpaceObject0(disaster.getId())!=null)
				space.destroySpaceObject(disaster.getId());
		}

		// If not finished but least one victim was treated
		// use random to determine if need to move to another position for next victim.
		if(victims==0 || cnt>0 && Math.random()>0.5)
		{
			obj.setProperty(PROPERTY_TREATED, new Double(0));
			setFinished(space, obj, true);
		}
		else
		{
			obj.setProperty(PROPERTY_TREATED, new Double(treated));			
		}
	}
}
