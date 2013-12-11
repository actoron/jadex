package jadex.bdiv3.examples.disastermanagement;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Deliver a patient at the hospital.
 */
public class DeliverPatientTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "deliver_patient";
	
	/** The treated property (of the space object). */
	public static final String	PROPERTY_TREATED = "treated";

	/** The patient property (of the space object). */
	public static final String	PROPERTY_PATIENT = "patient";

	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		// Update ambulance object based on time progress.
		double	treated	= ((Number)obj.getProperty(PROPERTY_TREATED)).doubleValue();
		treated	+= progress*0.0005;	// 1 patient per 2 seconds.
		if(treated>1)
		{
			obj.setProperty(PROPERTY_PATIENT, Boolean.FALSE);
			obj.setProperty(PROPERTY_TREATED, Double.valueOf(0));
			obj.setProperty("state", "idle");
			setFinished(space, obj, true);
		}
		else
		{
			obj.setProperty(PROPERTY_TREATED, Double.valueOf(treated));			
		}
	}
}
