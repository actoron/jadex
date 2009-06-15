package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 *  Process for continuously creating food.
 */
public class DefaultObjectCreationProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last executed tick. */
	protected double	lasttick;

	//-------- constructors --------
	
	/**
	 *  Create a new create food process.
	 */
	public DefaultObjectCreationProcess()
	{		
	}
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space)
	{
		this.lasttick	= clock.getTick();
		System.out.println("create food process started.");
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		System.out.println("create food process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
//		System.out.println("create food process called: "+deltat);
		
		Grid2D grid = (Grid2D)space;
		
		double	rate	= getProperty("rate")!=null? 
			((Number)getProperty("rate")).doubleValue(): 5.0;
		
		while(lasttick+rate<clock.getTick())
		{
			lasttick	+= rate;
			IVector2 pos = grid.getEmptyGridPosition();
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.PROPERTY_POSITION, pos);
				props.put("creation_age", new Double(clock.getTick()));
				props.put("clock", clock);
				grid.createSpaceObject("food", props, null);
//				System.out.println("Created food: "+obj);
			}
		}
	}
}
