package jadex.bdi.examples.coordination.ant;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Process creates packages with an optional rate at a random position within
 * the space
 */
public class PackageProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;
	int roundCounter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public PackageProcess() {
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
		System.out.println("1************************************************************");
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
		System.out.println("3************************************************************");
		if (delta > rate) {
			// Create new package
			lasttick = clock.getTick();

			// IVector2 pos = grid.getRandomPosition(Vector2Int.ZERO);
			// if (pos != null) {
			// Map props = new HashMap();
			// props.put(Space2D.POSITION, pos);
			// props.put("round", new Integer(0));
			// props.put("age", "Child");
			// grid.createSpaceObject("packageChild", props, null, null);
			//
			// }

			// Just testing
			// Map props = new HashMap();
			// props.put(Space2D.POSITION,
			// grid.getRandomPosition(Vector2Int.ZERO));
			// props.put("widtht", new Double(0.4));
			// props.put("round", new Integer(0));
			// props.put("height", new Double(0.2));
			// props.put("creation_age", new Double(clock.getTick()));
			// props.put("clock", clock);
			// props.put("imagePath", null);
			// // props.put("size", new Vector2Double(0.4, 0.4));
			// // width", "heigh
			// space.createSpaceObject("housePackage", props, null, null);

			// Update old packages
			// ISpaceObject[] objectsChild =
			// grid.getSpaceObjectsByType("packageChild");
			//			
			// for(int i=0; i < objectsChild.length; i++){
			// System.out.println(objectsChild[i].getId());
			// Integer round = (Integer)objectsChild[i].getProperty("round");
			// int roundInt = round.intValue();
			// roundInt++;
			// objectsChild[i].setProperty("round", new Integer(roundInt));
			// if(roundInt > 4){
			// pos = (IVector2) objectsChild[i].getProperty(Space2D.POSITION);
			// grid.destroySpaceObject(objectsChild[i].getId());
			// Map props = new HashMap();
			// props.put(Space2D.POSITION, pos);
			// props.put("round", new Integer(roundInt));
			// props.put("age", "Parent");
			// grid.createSpaceObject("packageParent", props, null, null);
			// }
			// }
			//			
			// ISpaceObject[] objectsParent =
			// grid.getSpaceObjectsByType("packageParent");
			// for(int i=0; i < objectsParent.length; i++){
			// System.out.println(objectsParent[i].getId());
			// Integer round = (Integer)objectsParent[i].getProperty("round");
			// int roundInt = round.intValue();
			// roundInt++;
			// objectsParent[i].setProperty("round", new Integer(roundInt));
			// if(roundInt > 8){
			// pos = (IVector2) objectsParent[i].getProperty(Space2D.POSITION);
			// grid.destroySpaceObject(objectsParent[i].getId());
			// Map props = new HashMap();
			// props.put(Space2D.POSITION, pos);
			// props.put("round", new Integer(roundInt));
			// props.put("age", "Grandparent");
			// grid.createSpaceObject("packageGrandParent", props, null, null);
			// }
			// }

			// //Update old packages
			// updateOldPackages(grid.getSpaceObjectsByType("packageChild"),
			// grid, "packageParent", 4);
			// updateOldPackages(grid.getSpaceObjectsByType("packageParent"),
			// grid, "packageGrandParent", 8);
			// updateOldPackages(grid.getSpaceObjectsByType("house"), grid,
			// "house", roundCounter);
			// updateOldPackagesByRemoving(grid.getSpaceObjectsByType("traceRoute"),
			// grid, "traceRoute", roundCounter);
			updateObjectProperties(grid.getSpaceObjectsByType("traceRoute"), grid);
		}
		roundCounter++;
	}

	/**
	 * Responsible for updating the image size of the packages according to
	 * their age. Therefore, the old object is destroyed and a new one with the
	 * same properties created.
	 * 
	 * @param objects
	 * @param space
	 * @param objectType
	 * @param border
	 *            the time when the packages get older, e.g. smaller image on
	 *            the screen
	 */
	private void updateOldPackagesByRemoving(ISpaceObject[] objects, IEnvironmentSpace space, String objectType, int border) {
		for (int i = 0; i < objects.length; i++) {
			// System.out.println(objects[i].getId());
			Integer round = (Integer) objects[i].getProperty(TraceRouteAction.ROUND);
			int roundInt = round.intValue();
			roundInt++;
			objects[i].setProperty("round", new Integer(roundInt));
			// System.out.println("RoundInt: " + roundInt + ", border: " +
			// border);
			if (roundInt >= border && !objectType.equals("house")) {
				IVector2 pos = (IVector2) objects[i].getProperty(Space2D.POSITION);
				space.destroySpaceObject(objects[i].getId());
				Map props = new HashMap();
				props.put(Space2D.POSITION, pos);
				props.put("round", new Integer(roundInt));
				props.put("age", objectType);
				space.createSpaceObject(objectType, props, null, null);
			}
		}
	}

	/**
	 * Updates the properties of the TraceRouteObject
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
