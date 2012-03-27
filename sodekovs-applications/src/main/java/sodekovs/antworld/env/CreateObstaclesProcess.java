package sodekovs.antworld.env;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.HashMap;
import java.util.Map;

/**
 * Process is responsible for the life cycle of the food source objects.
 */
public class CreateObstaclesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public CreateObstaclesProcess() {
		 System.out.println("Created Manage Food Sources Process!");
	}

	// -------- ISpaceProcess interface --------

	/**
	 * This method will be executed by the object before the process gets added to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space) {
//		this.lasttick = clock.getTick();
		ContinuousSpace2D mySpace = (ContinuousSpace2D) space;
		double stepSize = 0.001;
		IVector2 pos = new Vector2Double(0.5, 0.3);
		Map<String, Object> props = new HashMap<String, Object>();
		
		
		do{
			pos = new Vector2Double(pos.getXAsDouble(), pos.getYAsDouble()+stepSize);
			props.put(Space2D.PROPERTY_POSITION, pos);
			mySpace.createSpaceObject("obstacle", props, null);
			props = new HashMap<String, Object>();
			
		}while(pos.getYAsDouble()<=0.6);
		
		 System.out.println("Create Obstacles Process executed.");
	}

	/**
	 * This method will be executed by the object before the process is removed from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space) {
		// System.out.println("create package process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space) {
	}
}
