package sodekovs.marsworld.movement;

import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;
import jadex.extension.envsupport.environment.AbstractTask;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Move an object towards a destination.
 */
public class MoveTask extends AbstractTask {
	// -------- constants --------

	/** The destination property. */
	public static final String PROPERTY_TYPENAME = "move";

	/** The destination property. */
	public static final String PROPERTY_DESTINATION = "destination";

	/** The scope property. */
	public static final String PROPERTY_SCOPE = "scope";

	/** The speed property of the moving object (units per second). */
	public static final String PROPERTY_SPEED = "speed";

	/** The vision property of the moving object (radius in units). */
	public static final String PROPERTY_VISION = "vision";

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
		IVector2 destination = (IVector2) getProperty(PROPERTY_DESTINATION);
		final IBDIExternalAccess agent = (IBDIExternalAccess) getProperty(PROPERTY_SCOPE);

		double speed = ((Number) obj.getProperty(PROPERTY_SPEED)).doubleValue();
		double maxdist = progress * speed * 0.001;
		IVector2 loc = (IVector2) obj.getProperty(Space2D.PROPERTY_POSITION);
		// Todo: how to handle border conditions!?
		IVector2 newloc = ((Space2D) space).getDistance(loc, destination).getAsDouble() <= maxdist ? destination : destination.copy().subtract(loc).normalize().multiply(maxdist).add(loc);

		((Space2D) space).setPosition(obj.getId(), newloc);

		// Check, whether agent should walk randomly with or without remembering already visited positions.
		//Confer WalkingStrategyEnum for Mapping of int values to semantics.
		int walkingStrategyProperty =(Integer) space.getSpaceObjectsByType("walkingStrategy")[0].getProperty("strategy");
		if (walkingStrategyProperty> 0) {

			// ************************Hack for Agents: *******************************
			final IVector2 newLocation = newloc.copy();
			final DecimalFormat decimalFormat = new DecimalFormat("#0.0000");
			agent.scheduleStep(new IComponentStep<Void>() {
				public IFuture<Void> execute(IInternalAccess ia) {
					IBDIInternalAccess bia = (IBDIInternalAccess) ia;
					ISpaceObject[] homebases = ((ISpaceObject[]) ((Space2D) space).getSpaceObjectsByType("homebase"));
					HashMap map = (HashMap) homebases[0].getProperty("visitedPos");
					synchronized (map) {
						map.put(decimalFormat.format(newLocation.getXAsDouble()), decimalFormat.format(newLocation.getYAsDouble()));
						homebases[0].setProperty("visitedPos", map);
					}

					// Hack: Create trace
					Map props = new HashMap();
					props.put(Space2D.PROPERTY_POSITION, newLocation);
					space.createSpaceObject("trace", props, null);

					// System.out.println("#Sentry# MoveTask: " + map.size());
					// ((Space2D)space).get
					// }
					return IFuture.DONE;
				}
			});
			// *******************************
		}
		

		// Process vision at new location.
		double vision = ((Number) obj.getProperty(PROPERTY_VISION)).doubleValue();
		final Set objects = ((Space2D) space).getNearObjects((IVector2) obj.getProperty(Space2D.PROPERTY_POSITION), new Vector1Double(vision));
		if (objects != null) {
			agent.scheduleStep(new IComponentStep<Void>() {
				@Classname("add")
				public IFuture<Void> execute(IInternalAccess ia) {
					IBDIInternalAccess bia = (IBDIInternalAccess) ia;
					for (Iterator it = objects.iterator(); it.hasNext();) {
						final ISpaceObject so = (ISpaceObject) it.next();
						if (so.getType().equals("target")) {
							if (!bia.getBeliefbase().getBeliefSet("my_targets").containsFact(so)) {
								bia.getBeliefbase().getBeliefSet("my_targets").addFact(so);
							}
							// System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);

						}
					}
					return IFuture.DONE;
				}
			});
			// System.out.println("New target seen: "+scope.getAgentName()+", "+objects[i]);

		}

		if (newloc == destination)
			setFinished(space, obj, true);
	}
}
