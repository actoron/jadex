package jadex.bdi.examples.antworld;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.envsupport.environment.space2d.Grid2D;
import jadex.adapter.base.envsupport.environment.space2d.Space2D;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Int;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;

/**
 * Process is responsible for the life cycle of the Pheromones.
 */
public class ManagePheromonesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	protected double lasttick;
	int roundCounter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public ManagePheromonesProcess() {
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
			manageEvaporation(grid.getSpaceObjectsByType("pheromone"), grid);
//			managePropagation(grid.getSpaceObjectsByType("pheromone"), grid);
		}
		roundCounter++;
	}

	/**
	 * Responsible for updating the image size and responsible property of the
	 * trace routes according to their age. If the object is too old, if will be
	 * destroyed. In fact in manages the evaporation process of the pheromones.
	 * 
	 * @param objects
	 * @param space
	 */
	private void manageEvaporation(ISpaceObject[] objects, IEnvironmentSpace space) {
		for (int i = 0; i < objects.length; i++) {
			// System.out.println(objects[i].getId());
			Integer round = (Integer) objects[i].getProperty(ProducePheromoneAction.STRENGTH);
			int roundInt = round.intValue() - 1;
//			double reductionRate = 0.80;
//			int roundInt = new Integer((int) Math.round(round.intValue() * reductionRate)).intValue();
			if (roundInt <= 0) {
				space.destroySpaceObject(objects[i].getId());
			} else {
//				roundInt--;
				// roundInt--;
				objects[i].setProperty(ProducePheromoneAction.STRENGTH, new Integer(roundInt));
				// managePropagation(objects[i], space);
			}
		}
	}

	/**
	 * Responsible for updating the image size and responsible property of the
	 * trace routes according to their age. If the object is too old, if will be
	 * destroyed. In fact in manages the evaporation process of the pheromones.
	 * 
	 * @param objects
	 * @param space
	 */
	private void managePropagation(ISpaceObject[] objects, IEnvironmentSpace space) {

		for (int i = 0; i < objects.length; i++) {
			ISpaceObject object = objects[i];
			IVector2 pos = (IVector2) object.getProperty(Space2D.PROPERTY_POSITION);
			int px = pos.getXAsInteger();
			int py = pos.getYAsInteger();
			int pheromoneStrength = new Integer(object.getProperty(ProducePheromoneAction.STRENGTH).toString()).intValue();

			// The rate pheromones lose on propagation.
			double absorbationRate = 0.66;
			int newPheromoneStrength = new Integer((int) Math.round(pheromoneStrength * absorbationRate)).intValue();
			System.out.println("#ManagePheromonesProcess# Computes Strength: " + newPheromoneStrength);

			if (newPheromoneStrength > 0) {
				// Propagate Pheromone to all four directions:
				// UP
				// pos = new Vector2Int(px, py - 1);
				createPheromoneInSpace(new Vector2Int(px, py - 1), space, newPheromoneStrength);
				// DOWN
				// pos = new Vector2Int(px, py + 1);
				createPheromoneInSpace(new Vector2Int(px, py + 1), space, newPheromoneStrength);
				// LEFT
				// pos = new Vector2Int(px - 1, py);
				createPheromoneInSpace(new Vector2Int(px - 1, py), space, newPheromoneStrength);
				// RIGHT
				// pos = new Vector2Int(px + 1, py);
				createPheromoneInSpace(new Vector2Int(px + 1, py), space, newPheromoneStrength);
			}
		}
	}

	private void createPheromoneInSpace(IVector2 pos, IEnvironmentSpace space, int pheromoneStrength) {
		Collection pheromones = ((Grid2D) space).getSpaceObjectsByGridPosition(pos, "pheromone");
		ISpaceObject pheromone = (ISpaceObject) (pheromones == null ? null : pheromones.iterator().next());

		// create new pheromone
		if (pheromone == null) {
			Map props = new HashMap();
			props.put(Space2D.PROPERTY_POSITION, pos);
			props.put(ProducePheromoneAction.ANT_ID, "propagation");
			props.put(ProducePheromoneAction.STRENGTH, new Integer(pheromoneStrength));
			space.createSpaceObject("pheromone", props, null);
			System.out.println("#ManagePheromonesProcess# Created new Propagation Pheromone: " + pos.toString() + ", " + pheromoneStrength);
		}
		// update old pheromone
		else {
			// UPDATE
			int oldPheromoneStrength = new Integer(pheromone.getProperty(ProducePheromoneAction.STRENGTH).toString()).intValue();
			if ((pheromoneStrength + oldPheromoneStrength) < 10) {
				pheromone.setProperty(ProducePheromoneAction.STRENGTH, new Integer(pheromoneStrength + oldPheromoneStrength));
				System.out.println("#ManagePheromonesProcess# Updated Pheromone because of Propagation: " + pos.toString() + ", " + (pheromoneStrength + oldPheromoneStrength));
			} else {
				// pheromone.setProperty(ProducePheromoneAction.STRENGTH, new
				// Long(pheromoneStrenght > oldPheromoneStrength ?
				// pheromoneStrenght : oldPheromoneStrength));
				pheromone.setProperty(ProducePheromoneAction.STRENGTH, new Integer(10));
				System.out.println("#ManagePheromonesProcess# Updated Pheromone because of Propagation: " + pos.toString() + ", 10");
			}
			pheromone.setProperty(ProducePheromoneAction.ANT_ID, "propagation");
		}
	}
}
