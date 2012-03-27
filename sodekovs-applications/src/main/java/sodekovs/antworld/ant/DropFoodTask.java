package sodekovs.antworld.ant;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

/**
 * Task for dropping food on a nest field.
 */
public class DropFoodTask extends AbstractTask {
	// -------- constants --------

	/** The drop properties. */
	public static final String PROPERTY_TYPENAME = "drop";

	/** The IComponentIdentifier of the calling BDI Agent. */
	public static final String ACTOR_ID = "actor_id";

	// -------- IObjectTask methods --------

	/**
	 * Executes the task. Handles exceptions. Subclasses should implement doExecute() instead.
	 * 
	 * @param space
	 *            The environment in which the task is executing.
	 * @param obj
	 *            The object that is executing the task.
	 * @param progress
	 *            The time that has passed according to the environment executor.
	 */
	public void execute(final IEnvironmentSpace space, ISpaceObject obj, long progress, IClockService clock) {

		ContinuousSpace2D contSpace = (ContinuousSpace2D) space;
		long avatarId = (Long) getProperty(ACTOR_ID);
		ISpaceObject so = contSpace.getSpaceObject(avatarId);

//		assert (Boolean) so.getProperty("has_food") == true : so;

		// TODO: atomic action?
		ISpaceObject[] allNests = contSpace.getSpaceObjectsByType("nest");

		// Get the "right" nest.
		for (ISpaceObject nest : allNests) {
			if (nest.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(so.getProperty(ContinuousSpace2D.PROPERTY_POSITION))) {
//				System.out.println("Nest Pos: " + nest.getProperty(ContinuousSpace2D.PROPERTY_POSITION) + " ant Pos: " + so.getProperty(ContinuousSpace2D.PROPERTY_POSITION));
				int stock = (Integer) nest.getProperty("stock");
				nest.setProperty("stock", stock + 1);
				so.setProperty("has_food", false);
				break;
			}
		}
		setFinished(space, obj, true);

	}
}
