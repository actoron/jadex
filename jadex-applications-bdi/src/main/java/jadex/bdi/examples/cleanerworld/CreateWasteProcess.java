package jadex.bdi.examples.cleanerworld;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 *  Environment process for creating wastes.
 */
public class CreateWasteProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last waste creation time. */
	protected long	lasttime;
	
	//-------- constructors --------
	
	/**
	 *  Create a new create food process.
	 */
	public CreateWasteProcess()
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
		this.lasttime	= clock.getTime();
//		System.out.println("create waste process started.");
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
//		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
//		System.out.println("create waste process called: "+deltat);
		
		Space2D grid = (Space2D)space;
		
		long rate = getProperty("rate")!=null? 
			((Number)getProperty("rate")).longValue(): 3000;
		
		while(lasttime+rate<clock.getTime())
		{
			lasttime	+= rate;
			IVector2 pos = grid.getRandomPosition(Vector2Int.ZERO);
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.PROPERTY_POSITION, pos);
				grid.createSpaceObject("waste", props, null);
//				System.out.println("Created waste: "+obj);
			}
		}
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 * /
	public String toString()
	{
		return SReflect.getUnqualifiedClassName(this.getClass());
	}*/
}
