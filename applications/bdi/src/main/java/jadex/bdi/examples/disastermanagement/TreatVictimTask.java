package jadex.bdi.examples.disastermanagement;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;

/**
 *  Treat a victim at a disaster.
 */
public class TreatVictimTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "treat_victim";
	
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
		// Check if ambulance object is in range of victim.
		Space2D	space2d	= (Space2D)space;
		ISpaceObject	disaster	= (ISpaceObject)getProperty(PROPERTY_DISASTER);
		double	range	= ((Number)disaster.getProperty("size")).intValue()/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		if(space2d.getDistance((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION),
			(IVector2)disaster.getProperty(Space2D.PROPERTY_POSITION)).getAsDouble()>range*1.1) // allow for 10% rounding error
		{
			throw new RuntimeException("Victim out of range: "+obj);
		}
		
		// Only treat victims if area clear of chemicals
		int chemicals	= ((Number)disaster.getProperty("chemicals")).intValue();
		if(chemicals==0)
		{
			// Update disaster object based on time progress.
			int victims	= ((Number)disaster.getProperty("victims")).intValue();
			double	treated	= ((Number)obj.getProperty(PROPERTY_TREATED)).doubleValue();
			treated	+= progress*0.0002;	// 1 victim per 5 seconds.
			if(treated>1)
			{
				obj.setProperty(DeliverPatientTask.PROPERTY_PATIENT, Boolean.TRUE);
				victims	= Math.max(victims-1, 0);
				disaster.setProperty("victims", Integer.valueOf(victims));
			}
	
			// Remove disaster object when everything is now fine.
			if(victims==0 && ((Number)disaster.getProperty("fire")).intValue()==0 && ((Number)disaster.getProperty("chemicals")).intValue()==0)
			{
				if(space2d.getSpaceObject0(disaster.getId())!=null)
					space.destroySpaceObject(disaster.getId());
			}
	
			if(treated>1)
			{
				obj.setProperty(PROPERTY_TREATED, Double.valueOf(0));
				setFinished(space, obj, true);
			}
			else
			{
				obj.setProperty(PROPERTY_TREATED, Double.valueOf(treated));			
			}
		}
	}
}
