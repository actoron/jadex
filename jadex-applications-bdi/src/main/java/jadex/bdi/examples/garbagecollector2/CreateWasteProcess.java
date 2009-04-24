package jadex.bdi.examples.garbagecollector2;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 */
public class CreateWasteProcess extends SimplePropertyObject implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The last executed tick. */
	protected double	lasttick;
	
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
		this.lasttick	= clock.getTick();
		System.out.println("create waste process started.");
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
//		System.out.println("create waste process called: "+deltat);
		
		Grid2D grid = (Grid2D)space;
		
		double	delta	= clock.getTick() - lasttick;
		
		if(delta>2)
		{
			lasttick	= clock.getTick();
		
			IVector2 pos = grid.getRandomPosition(Vector2Int.ZERO);
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				grid.createSpaceObject("garbage", null, props, null, null);
//				System.out.println("Created waste: "+obj);
			}
		}
	}
}
