package jadex.bdi.examples.disasterrescue;

import jadex.application.space.envsupport.environment.AbstractTask;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.commons.service.clock.IClockService;

/**
 *  Clear chemicals at a disaster.
 */
public class ClearChemicalsTask extends AbstractTask
{
	//-------- constants --------
	
	/** The task name. */
	public static final String	PROPERTY_TYPENAME = "clear_chemicals";
	
	/** The disaster property. */
	public static final String	PROPERTY_DISASTER = "disaster";
	
	/** The cleared property (of the space object). */
	public static final String	PROPERTY_CLEARED = "cleared";

	//-------- IObjectTask methods --------
	
	/**
	 *  Executes the task.
	 *  @param space	The environment in which the task is executing.
	 *  @param obj	The object that is executing the task.
	 *  @param progress	The time that has passed according to the environment executor.
	 */
	public void execute(IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock)
	{
		// Check if engine object is in rage of chemicals.
		Space2D	space2d	= (Space2D)space;
		ISpaceObject	disaster	= (ISpaceObject)getProperty(PROPERTY_DISASTER);
		double	range	= ((Number)disaster.getProperty("size")).intValue()/2 * 0.005;	// 0.005 = scale of drawsize in application.xml
		if(space2d.getDistance((IVector2)obj.getProperty(Space2D.PROPERTY_POSITION),
			(IVector2)disaster.getProperty(Space2D.PROPERTY_POSITION)).getAsDouble()>range)
		{
			throw new RuntimeException("Chemcials out of range: "+obj);
		}

		// Update disaster object based on time progress.
		int	cnt	= 0;
		double	cleared	= ((Number)obj.getProperty(PROPERTY_CLEARED)).doubleValue();
		cleared	+= progress*0.0002;	// 1 chemical per 5 seconds.
		while(cleared>1)
		{
			cnt++;
			cleared	-= 1;
		}
		int chemicals	= ((Number)disaster.getProperty("chemicals")).intValue();
		chemicals	= Math.max(chemicals-cnt, 0);
		disaster.setProperty("chemicals", new Integer(chemicals));

		// If not finished but least one fire was extinguished
		// use random to determine if need to move to another position for next fire.
		if(chemicals==0 || cnt>0 && Math.random()>0.5)
		{
			obj.setProperty(PROPERTY_CLEARED, new Double(0));
			setFinished(space, obj, true);
		}
		else
		{
			obj.setProperty(PROPERTY_CLEARED, new Double(cleared));			
		}
	}
}
