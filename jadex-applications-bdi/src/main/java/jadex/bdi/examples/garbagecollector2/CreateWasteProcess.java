package jadex.bdi.examples.garbagecollector2;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceAction;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bdi.examples.garbagecollector.Environment;
import jadex.bdi.examples.garbagecollector.Position;

/**
 * 
 */
public class CreateWasteProcess implements ISpaceProcess
{
	protected IVector1 delta;
	
	/**
	 * 
	 */
	public CreateWasteProcess()
	{
		this.delta = Vector1Double.ZERO;
	}
	
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void start(IEnvironmentSpace space)
	{
		System.out.println("create waste process started.");
	}

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		System.out.println("create waste process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param time the current time
	 * @param deltat time passed during this step
	 * @param space the space this process is running in
	 */
	public void execute(long time, IVector1 deltat, IEnvironmentSpace space)
	{
//		System.out.println("create waste process called: "+time);
		
		Grid2D grid = (Grid2D)space;
		
		delta.add(deltat);
		
		if(delta.getAsDouble()>1)
		{
			delta.subtract(new Vector1Double(1));
		
			IVector2 pos = grid.getRandomPosition(Vector2Int.ZERO);
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				Object obj = grid.createSpaceObject("garbage", null, props, null, null);
				System.out.println("Created garbage: "+obj);
			}
		}
	}


	/**
	 * Returns the ID of the action.
	 * @return ID of the action
	 */
	public Object getId()
	{
		// todo: remove here or from application xml?
		return "create";
	}
}
