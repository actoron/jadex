package jadex.bdi.examples.hunterprey_env;

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
public class CreateFoodProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last executed tick. */
	protected double	lasttick;

	//-------- constructors --------
	
	/**
	 *  Create a new create food process.
	 */
	public CreateFoodProcess()
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
		
		double	delta	= clock.getTick() - lasttick;
		
		int rate = getProperty("rate")!=null? 
			((Integer)getProperty("rate")).intValue(): 5;
		
		if(delta>rate)
		{
			lasttick	= clock.getTick();
		
			IVector2 pos = grid.getEmptyGridPosition();
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				grid.createSpaceObject("food", props, null, null);
//				System.out.println("Created food: "+obj);
			}
		}
	}
}
