package sodekovs.antworld.env;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Process is responsible for the life cycle of the pheromones.
 */
public class ManagePheromonesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	/** The last executed tick. */
	private double lasttickEvaporation;
	private double lasttickPropagation;

	// int roundCounter = 0;

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public ManagePheromonesProcess() {
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
		this.lasttickEvaporation = clock.getTick();
		this.lasttickPropagation = clock.getTick();
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

		// Grid2D grid = (Grid2D) space;
		//
		double deltaEvaporation = clock.getTick() - lasttickEvaporation;
		double deltaPropagation = clock.getTick() - lasttickPropagation;
		//
		int rateEvaporation = getProperty("rate") != null ? ((Integer) getProperty("rate")).intValue() : 5;
		int ratePropagation = 15;
		// // System.out.println("LastTick: " + lasttick + "-->roundCount: " +

		if (deltaEvaporation > rateEvaporation) {
			lasttickEvaporation = clock.getTick();
			manageEvaporation(space.getSpaceObjectsByType("pheromone"), space);
		}
		if (deltaPropagation > ratePropagation) {
			// // Update "age" of trace route objects
			lasttickPropagation = clock.getTick();
//			managePropagation(space.getSpaceObjectsByType("pheromone"), space);
		}
		// roundCounter++;
	}

	/**
	 * Responsible for updating the image size and responsible property of the trace routes according to their age. If the object is too old, if will be destroyed. In fact in manages the evaporation
	 * process of the pheromones.
	 * 
	 * @param objects
	 * @param space
	 */
	private void manageEvaporation(ISpaceObject[] objects, IEnvironmentSpace space) {
		for (int i = 0; i < objects.length; i++) {
//			System.out.println(objects[i].getId());
			Integer strength = (Integer) objects[i].getProperty("strength");
			// int roundInt = round.intValue()-1;
			double reductionRate = 0.90;
			int newStrength = new Integer((int) Math.floor(strength.intValue() * reductionRate)).intValue();
			if (newStrength <= 1) {
//				System.out.println("Destroying SpaceObject: " + objects[i].getId());
				space.destroySpaceObject(objects[i].getId());
			} else {
				// roundInt--;
				// roundInt--;
				objects[i].setProperty("strength", new Integer(newStrength));
//				System.out.println("new strength:" + newStrength);
				// managePropagation(objects[i], space);
			}
		}
	}

	/**
	 * Responsible for updating the image size and responsible property of the trace routes according to their age. If the object is too old, if will be destroyed. In fact in manages the evaporation
	 * process of the pheromones.
	 * 
	 * @param existingPheromones
	 * @param space
	 */
	private void managePropagation(ISpaceObject[] existingPheromones, IEnvironmentSpace space) {
		ArrayList pheromonesToUpdate = new ArrayList();
		Object[] tmp = new Object[2];

		for (int i = 0; i < existingPheromones.length; i++) {
			ISpaceObject object = existingPheromones[i];
			IVector2 pos = (IVector2) object.getProperty(Space2D.PROPERTY_POSITION);
			double px = pos.getXAsDouble();
			double py = pos.getYAsDouble();
			int pheromoneStrength = new Integer(object.getProperty("strength").toString()).intValue();

			// Divide pheromone strength according to the number of grids it is
			// propagated within the next step.
			int newPheromoneStrength = new Integer((int) Math.round(pheromoneStrength / 4)).intValue();

			// The rate pheromones lose on propagation.
			double absorbationRate = 1.0;
			newPheromoneStrength = new Integer((int) Math.round(newPheromoneStrength * absorbationRate)).intValue();
			// System.out.println("#ManagePheromonesProcess-Evaporation# Computed Strength: "
			// + newPheromoneStrength + " for source Pheromone: " +
			// object.toString());

			if (newPheromoneStrength > 0) {
				// Propagate Pheromone to all four directions:
				// UP
				// pos = new Vector2Int(px, py - 1);
				// createPheromoneInSpace(new Vector2Int(px, py - 1), space,
				// newPheromoneStrength);
				tmp = new Object[2];
				tmp[0] = new Vector2Double(px, py - 0.01);
				tmp[1] = new Integer(newPheromoneStrength);
				pheromonesToUpdate.add(tmp);
				// DOWN
				// pos = new Vector2Int(px, py + 1);
				// createPheromoneInSpace(new Vector2Int(px, py + 1), space,
				// newPheromoneStrength);
				tmp = new Object[2];
				tmp[0] = new Vector2Double(px, py + 0.01);
				tmp[1] = new Integer(newPheromoneStrength);
				pheromonesToUpdate.add(tmp);
				// LEFT
				// pos = new Vector2Int(px - 1, py);
				// createPheromoneInSpace(new Vector2Int(px - 1, py), space,
				// newPheromoneStrength);
				tmp = new Object[2];
				tmp[0] = new Vector2Double(px - 0.01, py);
				tmp[1] = new Integer(newPheromoneStrength);
				pheromonesToUpdate.add(tmp);
				// RIGHT
				// pos = new Vector2Int(px + 1, py);
				// createPheromoneInSpace(new Vector2Int(px + 1, py), space,
				// newPheromoneStrength);
				tmp = new Object[2];
				tmp[0] = new Vector2Double(px + 0.01, py);
				tmp[1] = new Integer(newPheromoneStrength);
				pheromonesToUpdate.add(tmp);

//				System.out.println("\n#TEstList# Begin");
//				for (int j = 0; j < pheromonesToUpdate.size(); j++) {
//					Object[] tmp1 = (Object[]) pheromonesToUpdate.get(j);
//					System.out.println(j + " : " + (IVector2) tmp1[0] + " - " + tmp1[1].toString());
//				}
//				System.out.println("\n#TEstList# End");
			}
		}
		createPheromonesInSpace(pheromonesToUpdate, space);
	}

	private void createPheromonesInSpace(ArrayList values, IEnvironmentSpace space) {
		for (int i = 0; i < values.size(); i++) {
			Object[] tmp = (Object[]) values.get(i);
			createPheromoneInSpace((IVector2) tmp[0], space, ((Integer) tmp[1]).intValue());
		}
	}

	private void createPheromoneInSpace(IVector2 pos, IEnvironmentSpace space, int pheromoneStrength) {

		// Collection pheromones = ((Grid2D) space).getSpaceObjectsByGridPosition(pos, "pheromone");
		Object[] pheromones = ((Space2D) space).getNearObjects(pos, new Vector1Double(0.0), "pheromone").toArray();
		// ISpaceObject pheromone = (ISpaceObject) (pheromones == null ? null : pheromones.iterator().next());

		// Grid2D sp = (Grid2D) space;
		// sp.getSpaceObjectsByGridPosition(position, type);

		// create new pheromone
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(Space2D.PROPERTY_POSITION, pos);
		// props.put("ANT_ID", "propagation");
		// add old strength, if there was already a pheromone on that position. destroy old pheromone.
		if (pheromones.length > 0) {
			// iterate through array, add strenghts of existing pheromones and destroy them
			int oldPheromoneStrength = 0;
			for (Object existingPheromone : pheromones) {
				oldPheromoneStrength += new Integer(((ISpaceObject) existingPheromone).getProperty("strength").toString()).intValue();
				space.destroySpaceObject(((ISpaceObject) existingPheromone).getId());
			}
			// int oldPheromoneStrength = new Integer(pheromone.getProperty("strength").toString()).intValue();
			props.put("strength", new Integer(pheromoneStrength + oldPheromoneStrength));
			// System.out.println("Destroying SpaceObject: " + pheromone);
			// space.destroySpaceObject(pheromone.getId());
		} else {
			props.put("strength", new Integer(pheromoneStrength));
		}
		// System.out.println("Creating SpaceObject: " + props);
		space.createSpaceObject("pheromone", props, null);
		// System.out.println("#ManagePheromonesProcess# Created new Propagation Pheromone: "
		// + pos.toString() + ", " + pheromoneStrength);

		// update old pheromone

		// UPDATE

		// if ((pheromoneStrength + oldPheromoneStrength) < 10) {

		// System.out.println("#ManagePheromonesProcess# Updated Pheromone because of Propagation: "
		// + pos.toString() + ", " + (pheromoneStrength +
		// oldPheromoneStrength));

		// pheromone.setProperty(ProducePheromoneAction.STRENGTH, new
		// Long(pheromoneStrenght > oldPheromoneStrength ?
		// pheromoneStrenght : oldPheromoneStrength));
		// pheromone.setProperty(ProducePheromoneAction.STRENGTH, new
		// Integer(10));
		// pheromone.setProperty(ProducePheromoneAction.STRENGTH, new
		// Integer(pheromoneStrength + oldPheromoneStrength));
		// System.out.println("#ManagePheromonesProcess# Updated Pheromone because of Propagation wit max Value: "
		// + pos.toString() + ", 10");

		// pheromone.setProperty(ProducePheromoneAction.ANT_ID, "propagation");

	}
}
