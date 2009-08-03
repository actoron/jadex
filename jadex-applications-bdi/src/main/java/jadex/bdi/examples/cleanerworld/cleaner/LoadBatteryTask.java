package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.adapter.base.envsupport.environment.AbstractTask;
import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;

/**
 *  Move an object towards a destination.
 */
public class LoadBatteryTask extends AbstractTask
{
	//-------- constants --------
	
	/** The destination property. */
	public static final String	PROPERTY_TYPENAME = "load";
	
	/** The property for the target. */
	public static final String PROPERTY_TARGET = "target";

	
	/** The time required for loading full energy. */
	public static final int	TIME = 10000;
	
	/** The property for the charge state. */
	public static final String PROPERTY_CHARGESTATE = "chargestate";
	
	//-------- attributes --------
	
	/** The remaining time. */
	protected int	time;
	
	//-------- AbstractTask methods --------
	
	/**
	 *  Executes the task.
	 *  Handles exceptions. Subclasses should implement doExecute() instead.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
		ISpaceObject target	= (ISpaceObject)getProperty(PROPERTY_TARGET);
		IVector2 loc = (IVector2)obj.getProperty(Space2D.PROPERTY_POSITION);
		IVector2 tloc = (IVector2)target.getProperty(Space2D.PROPERTY_POSITION);
		if(!loc.equals(tloc))
			throw new RuntimeException("Not at location: "+obj+", "+target);
		
		double chargestate = ((Number)obj.getProperty(PROPERTY_CHARGESTATE)).doubleValue();
	
		if(chargestate<1.0)
		{
			double inc = progress.getAsDouble()/TIME;
			chargestate = Math.min(1.0, chargestate+inc);
		}
		
		obj.setProperty(PROPERTY_CHARGESTATE, new Double(chargestate));
//		System.out.println("Increased chargestate to: "+chargestate);
		
		if(chargestate==1.0)
			setFinished(space, obj, true);
	}
}
