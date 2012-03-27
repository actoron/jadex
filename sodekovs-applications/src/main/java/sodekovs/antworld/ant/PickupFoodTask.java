package sodekovs.antworld.ant;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

/**
 * Task for picking up food.
 */
public class PickupFoodTask extends AbstractTask {
	// -------- constants --------

	/** The destination property. */
	public static final String PROPERTY_TYPENAME = "pickup";

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
//		boolean ret = false;

		ContinuousSpace2D contSpace = (ContinuousSpace2D) space;
		long avatarId = (Long) getProperty(ACTOR_ID);
		ISpaceObject so = contSpace.getSpaceObject(avatarId);

		assert (Boolean) so.getProperty("has_food") == false : so;

		// TODO: atomic action?
		ISpaceObject[] allFoodSources = contSpace.getSpaceObjectsByType("food");

		// Get the "right" food source
		for (ISpaceObject foodSource : allFoodSources) {
			if (foodSource.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(so.getProperty(ContinuousSpace2D.PROPERTY_POSITION))) {
//				System.out.println("Food Source Pos: " + foodSource.getProperty(ContinuousSpace2D.PROPERTY_POSITION) + " ant Pos: " + so.getProperty(ContinuousSpace2D.PROPERTY_POSITION));
				int foodPieces = (Integer) foodSource.getProperty("food_pieces");
				if (foodPieces > 0) {
					foodSource.setProperty("food_pieces", foodPieces - 1);
					so.setProperty("has_food", true);

					// Support evaluation
					// int carriedFood = ((Integer)
					// so.getProperty("eval:carriedFood")).intValue();
					// so.setProperty("eval:carriedFood", new
					// Integer(carriedFood + 1));

				}else{
					System.out.println("did not pick up food since it was already empty...");
				}
				break;
			}
		}
		setFinished(space, obj, true);

	}
}
