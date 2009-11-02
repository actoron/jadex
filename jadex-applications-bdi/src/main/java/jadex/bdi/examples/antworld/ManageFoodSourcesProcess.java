package jadex.bdi.examples.antworld;

import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.service.clock.IClockService;
import jadex.commons.SimplePropertyObject;

/**
 * Process is responsible for the life cycle of the food source objects.
 */
public class ManageFoodSourcesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;
	int roundCounter = 0;
	boolean tmp = true;
	int tmpCount = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public ManageFoodSourcesProcess() {
		// System.out.println("Created Process!");
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
		this.lasttick = clock.getTick();
		// System.out.println("create package process started.");
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
		Grid2D grid = (Grid2D) space;

		double delta = clock.getTick() - lasttick;

		int rate = getProperty("rate") != null ? ((Integer) getProperty("rate")).intValue() : 1;
		// System.out.println("LastTick: " + lasttick + "-->roundCount: " +
		// roundCounter);
		if (delta > rate) {
			// Destroy empty food sources.
			lasttick = clock.getTick();
			ISpaceObject[] foodSources = (grid.getSpaceObjectsByType("foodSource"));
			for (int i = 0; i < foodSources.length; i++) {
				ISpaceObject foodSource = foodSources[i];
				Boolean empty = (Boolean) foodSource.getProperty("empty");
				if (empty.booleanValue()) {
					grid.destroySpaceObject(foodSource.getId());
					System.out.println("#ManageFoodSourceProcess# Food Source empty. Will be destroyed: " + foodSource);
				}
			}
			
//			if(tmp){
//			Map props = new HashMap();
//			props.put(Space2D.PROPERTY_POSITION, new Vector2Int(4, 4));			
//			space.createSpaceObject("food", props, null);
//			System.out.println("#ManageFoodSourceProcess# Created food..." + props);
//			
//			Map props2 = new HashMap();
//			props2.put(Space2D.PROPERTY_POSITION, new Vector2Int(5, 5));			
//			space.createSpaceObject("food", props2, null);
//			System.out.println("#ManageFoodSourceProcess# Created food..." + props2);
//
////			System.out.println("*******************");
////			ISpaceObject[] tt = (grid.getSpaceObjectsByType("food"));
////			for(int i=0; i < tt.length; i++){
////				System.out.println(tt[i]);	
////			}
//			
////			System.out.println("*******************");
//				tmp = false;
//			}else if(tmpCount == 105){
//				Map props = new HashMap();
//				props.put(Space2D.PROPERTY_POSITION, new Vector2Int(6, 6));			
//				space.createSpaceObject("food", props, null);
//				System.out.println("#ManageFoodSourceProcess# Created food..." + props);
//			}
//			tmpCount++;
		}
	}
}
