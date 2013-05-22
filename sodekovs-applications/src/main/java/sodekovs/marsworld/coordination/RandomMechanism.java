/**
 * 
 */
package sodekovs.marsworld.coordination;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * This mechanism randomly selects a matching receiver.
 * 
 * @author Thomas Preisler
 */
public class RandomMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	/** Deco link receiver mapping */
	protected Map<String, String> receiverMapping = null;

	protected ISpaceObject soMessages = null;
	
	/**
	 * Constructor.
	 * 
	 * @param space
	 *            the coordination space
	 */
	public RandomMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		
		this.soMessages = this.appSpace.getSpaceObjectsByType("soMessages")[0];
		
		this.eventNumber = 0;

		this.receiverMapping = new HashMap<String, String>();
		this.receiverMapping.put("latest_target_seen_nearest", "sentry");
		this.receiverMapping.put("latest_target_analyzed_nearest", "producer");
		this.receiverMapping.put("latest_target_produced_nearest", "carry");
		this.receiverMapping.put("latest_target_seen_random", "sentry");
		this.receiverMapping.put("latest_target_analyzed_random", "producer");
		this.receiverMapping.put("latest_target_produced_random", "carry");
	}

	@Override
	public void start() {
		// nothing to be done here
	}

	@Override
	public void stop() {
		// nothing to be done here
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;
		// get the type of the receiver
		String realization = (String) ci.getValueByName("dmlRealizationName");
		String receiverType = receiverMapping.get(realization);
		if (realization.equals("latest_target_seen_nearest") || realization.equals("latest_target_seen_random")) {
			soMessages.setProperty("targetSeen", (Integer) soMessages.getProperty("targetSeen") + 1);
		} else if (realization.equals("latest_target_analyzed_nearest") || realization.equals("latest_target_analyzed_random")) {
			soMessages.setProperty("targetAnalyzed", (Integer) soMessages.getProperty("targetAnalyzed") + 1);
		} else if (realization.equals("latest_target_produced_nearest") || realization.equals("latest_target_produced_random")) {
			soMessages.setProperty("targetProduced", (Integer) soMessages.getProperty("targetProduced") + 1);
		}
		
		// get all space objects which match the receiver typ
		ISpaceObject[] spaceObjects = appSpace.getSpaceObjectsByType(receiverType);
		// randomly select one
		Random rnd = new Random();
		ISpaceObject receiverObject = spaceObjects[rnd.nextInt(spaceObjects.length)];
		// build the receiver list for the coordination space
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
		IComponentDescription componentDescription = (IComponentDescription) receiverObject.getProperty("owner");
		receiver.add(componentDescription);
		space.publishCoordinationEvent(ci, receiver, getRealisationName(), ++eventNumber);
	}
}