/**
 * 
 */
package sodekovs.marsworld.coordination;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * This mechanism informs the nearest agent with a matching type.
 * 
 * @author Thomas Preisler
 */
public class NearestMechanism extends CoordinationMechanism {

	private static final int ANALYZED_TARGETS_BEFORE_FAILURE = 200;
	
	private static final int FAILURE_TIMER = 60000;

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	/** Deco link receiver mapping */
	protected Map<String, String> receiverMapping = null;

	protected ISpaceObject soMessages = null;
	protected ISpaceObject adaptation = null;

	protected int noTargetsAnalyzed = 0;

	protected boolean failure = false;

	protected List<CoordinationInfo> storedCI = null;

	/**
	 * Constructor.
	 * 
	 * @param space
	 *            the coordination space
	 */
	public NearestMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");

		this.soMessages = this.appSpace.getSpaceObjectsByType("soMessages")[0];
		this.adaptation = this.appSpace.getSpaceObjectsByType("adaptation")[0];

		this.eventNumber = 0;

		this.receiverMapping = new HashMap<String, String>();
		this.receiverMapping.put("latest_target_seen_nearest", "sentry");
		this.receiverMapping.put("latest_target_analyzed_nearest", "producer");
		this.receiverMapping.put("latest_target_produced_nearest", "carry");
		this.receiverMapping.put("latest_target_seen_random", "sentry");
		this.receiverMapping.put("latest_target_analyzed_random", "producer");
		this.receiverMapping.put("latest_target_produced_random", "carry");

		this.storedCI = new ArrayList<CoordinationInfo>();
	}

	@Override
	public void start() {
		// wait for some time, then simulate a failure
		applicationInterpreter.waitForDelay(FAILURE_TIMER, new IComponentStep<Void>() {

			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				if (!failure) {
					failure = true;
					adaptation.setProperty("failures", 1);
				}
				return IFuture.DONE;
			}
		});
	}

	@Override
	public void stop() {
		// when the mechanism is stopped all coordination info that are still pending after the failure will be send to prevent message lost in the MarsWorld4SASO app
		for (CoordinationInfo ci : storedCI) {
			// get the type of the receiver
			String realization = (String) ci.getValueByName("dmlRealizationName");
			String receiverType = receiverMapping.get(realization);
			if (realization.equals("latest_target_seen_nearest") || realization.equals("latest_target_seen_random")) {
				soMessages.setProperty("targetSeen", (Integer) soMessages.getProperty("targetSeen") + 1);
			} else if (realization.equals("latest_target_analyzed_nearest") || realization.equals("latest_target_analyzed_random")) {
				soMessages.setProperty("targetAnalyzed", (Integer) soMessages.getProperty("targetAnalyzed") + 1);
				noTargetsAnalyzed++;
			} else if (realization.equals("latest_target_produced_nearest") || realization.equals("latest_target_produced_random")) {
				soMessages.setProperty("targetProduced", (Integer) soMessages.getProperty("targetProduced") + 1);
			}

			// extract the space object from the coordination info
			CoordinationSpaceData data = (CoordinationSpaceData) ci.getValueByName("value");
			// get the position
			IVector2 pos = new Vector2Double(data.getX(), data.getY());
			// get the nearest object
			ISpaceObject spaceObject = appSpace.getNearestObject(pos, null, receiverType);
			// build the receiver list for the coordination space
			List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
			IComponentDescription componentDescription = (IComponentDescription) spaceObject.getProperty("owner");
			receiver.add(componentDescription);
			space.publishCoordinationEvent(ci, receiver, getRealisationName(), ++eventNumber);
		}
	}

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;

		if (noTargetsAnalyzed == ANALYZED_TARGETS_BEFORE_FAILURE && !failure) {
			failure = true;
			adaptation.setProperty("failures", (Integer) adaptation.getProperty("failures") + 1);
		}

		if (!failure) {
			// get the type of the receiver
			String realization = (String) ci.getValueByName("dmlRealizationName");
			String receiverType = receiverMapping.get(realization);
			if (realization.equals("latest_target_seen_nearest") || realization.equals("latest_target_seen_random")) {
				soMessages.setProperty("targetSeen", (Integer) soMessages.getProperty("targetSeen") + 1);
			} else if (realization.equals("latest_target_analyzed_nearest") || realization.equals("latest_target_analyzed_random")) {
				soMessages.setProperty("targetAnalyzed", (Integer) soMessages.getProperty("targetAnalyzed") + 1);
				noTargetsAnalyzed++;
			} else if (realization.equals("latest_target_produced_nearest") || realization.equals("latest_target_produced_random")) {
				soMessages.setProperty("targetProduced", (Integer) soMessages.getProperty("targetProduced") + 1);
			}

			// extract the space object from the coordination info
			CoordinationSpaceData data = (CoordinationSpaceData) ci.getValueByName("value");
			// get the position
			IVector2 pos = new Vector2Double(data.getX(), data.getY());
			// get the nearest object
			ISpaceObject spaceObject = appSpace.getNearestObject(pos, null, receiverType);
			// build the receiver list for the coordination space
			List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
			IComponentDescription componentDescription = (IComponentDescription) spaceObject.getProperty("owner");
			receiver.add(componentDescription);
			space.publishCoordinationEvent(ci, receiver, getRealisationName(), ++eventNumber);
		} else {
			storedCI.add(ci);
		}
	}
}