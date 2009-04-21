package jadex.bdi.examples.hunterprey_env;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector2Int;

import java.util.HashMap;
import java.util.Map;

/**
 *  Process for continuously creating food.
 */
public class CreateFoodProcess implements ISpaceProcess
{
	//-------- attributes --------
	
	/** The time passed. */
	protected IVector1 delta;
	
	//-------- constructors --------
	
	/**
	 *  Create a new create food process.
	 */
	public CreateFoodProcess()
	{
		this.delta = Vector1Double.ZERO;
	}
	
	//-------- ISpaceProcess interface --------
	
	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void start(IEnvironmentSpace space)
	{
		System.out.println("create food process started.");
	}

	/**
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
	 * 
	 * @param space the space this process is running in
	 */
	public void shutdown(IEnvironmentSpace space)
	{
		System.out.println("create food process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param time the current time
	 * @param deltat time passed during this step
	 * @param space the space this process is running in
	 */
	public void execute(IVector1 deltat, IEnvironmentSpace space)
	{
		System.out.println("create food process called: "+deltat);
		
		Grid2D grid = (Grid2D)space;
		
		delta.add(deltat);
		
		if(delta.getAsDouble()>2)
		{
			delta.subtract(new Vector1Double(2));
		
			IVector2 pos = grid.getEmptyGridPosition();
			if(pos!=null)
			{
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				Object obj = grid.createSpaceObject("food", null, props, null, null);
				System.out.println("Created food: "+obj);
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
		return "create-food";
	}
}
