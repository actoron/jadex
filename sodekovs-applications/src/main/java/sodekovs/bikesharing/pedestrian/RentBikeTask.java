package sodekovs.bikesharing.pedestrian;

import jadex.bridge.service.types.clock.IClockService;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;

/**
 * Task for dropping food on a nest field.
 */
public class RentBikeTask extends AbstractTask {
	// -------- constants --------

	/** The drop properties. */
	public static final String PROPERTY_TYPENAME = "rent";

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
		ISpaceObject myself = contSpace.getSpaceObject(avatarId);

//		assert (Boolean) so.getProperty("has_food") == true : so;

		// TODO: atomic action?
		ISpaceObject[] allBiketations = contSpace.getSpaceObjectsByType("bikestation");
		
//		synchronized (allBiketations) {
		
		StringBuffer buf = new StringBuffer();
		buf.append("Tries to rent bike: " + myself.getId() + "\n");

		// Get the "right" nest.
		for (ISpaceObject bikestation : allBiketations) {
			if (bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION))) {
//				System.out.println("Nest Pos: " + nest.getProperty(ContinuousSpace2D.PROPERTY_POSITION) + " ant Pos: " + so.getProperty(ContinuousSpace2D.PROPERTY_POSITION));
				int stock = (Integer) bikestation.getProperty("stock");
				//TODO: assert: stock > 1 before renting
				bikestation.setProperty("stock", stock - 1);
				myself.setProperty("drives_bike", true);
				buf.append(bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION) + " vs.\t" + myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION)+"\t");
				buf.append(bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION))+"\n");
				break;
			}else{
				buf.append(bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION) + " vs.\t" + myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION) +"\t");
				buf.append(bikestation.getProperty(ContinuousSpace2D.PROPERTY_POSITION).equals(myself.getProperty(ContinuousSpace2D.PROPERTY_POSITION))+"\n");
			}
		}
//		System.out.println(buf.toString());
		setFinished(space, obj, true);
		}
//	}
}
