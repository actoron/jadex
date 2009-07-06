package jadex.bdi.examples.antworld.foraging;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

/**
 * Process is responsible for the life cycle of trace route objects.
 */
public class ManageTraceRoutesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;
	int roundCounter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public ManageTraceRoutesProcess() {
		// System.out.println("Created Process!");
	}

	// -------- ISpaceProcess interface --------

	/**
	 * This method will be executed by the object before the process gets added
	 * to the execution queue.
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
	 * This method will be executed by the object before the process is removed
	 * from the execution queue.
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

		int rate = getProperty("rate") != null ? ((Integer) getProperty("rate")).intValue() : 5;
		// System.out.println("LastTick: " + lasttick + "-->roundCount: " +
		// roundCounter);
		if (delta > rate) {
			// Update "age" of trace route objects
			lasttick = clock.getTick();
			updateObjectProperties(grid.getSpaceObjectsByType("traceRoute"), grid);
		}
		roundCounter++;
	}

	/**
	 * Responsible for updating the image size and responsible property of the trace routes according to
	 * their age. If the object is too old, if will be destroyed.
	 * 
	 * @param objects
	 * @param space
	 */
	private void updateObjectProperties(ISpaceObject[] objects, IEnvironmentSpace space) {
		for (int i = 0; i < objects.length; i++) {
			// System.out.println(objects[i].getId());
			Integer round = (Integer) objects[i].getProperty(TraceRouteAction.ROUND);
			int roundInt = round.intValue();
			if (roundInt >= 15) {
				space.destroySpaceObject(objects[i].getId());
			} else {
				roundInt++;
				objects[i].setProperty("round", new Integer(roundInt));
			}
		}
	}
}
