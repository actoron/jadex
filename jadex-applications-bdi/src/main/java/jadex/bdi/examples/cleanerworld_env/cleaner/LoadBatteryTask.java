package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bdi.examples.marsworld_env.movement.ListenableTask;
import jadex.commons.concurrent.IResultListener;

/**
 *  Move an object towards a destination.
 */
public class LoadBatteryTask extends ListenableTask
{
	//-------- constants --------
	
	/** The time required for loading full energy. */
	public static final int	TIME = 10000;
	
	/** The property for the charge state. */
	public static final String PROPERTY_CHARGESTATE = "chargestate";
	
	//-------- attributes --------
	
	/** The target. */
	protected ISpaceObject	target;
	
	/** The remaining time. */
	protected int	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a new move task.
	 *  @param target	The target or home base.
	 *  @param load	The loading (or unloading) flag. 
	 *  @param listsner	The result listener to be informed when the destination is reached. 
	 */
	public LoadBatteryTask(ISpaceObject target, IResultListener listener)
	{
		super(listener);
		this.target	= target;
	}
	
	//-------- ListenableTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void	doExecute(IEnvironmentSpace space, ISpaceObject obj, IVector1 progress)
	{
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
			taskFinished(obj, null);
	}
}
