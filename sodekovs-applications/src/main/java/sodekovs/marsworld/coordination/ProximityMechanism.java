package sodekovs.marsworld.coordination;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationInfo;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A proximity based coordination medium. Only agents which a within the proximity range will be informed.
 * 
 * @author Thomas Preisler
 */
public class ProximityMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;

	/** The number of published events */
	protected Integer eventNumber = null;

	protected Map<String, String> receiverMapping = null;
	
	protected ISpaceObject energyCosts = null;

	public ProximityMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		this.energyCosts = appSpace.getSpaceObjectsByType("energyCosts")[0];

		this.eventNumber = 0;

		this.receiverMapping = new HashMap<String, String>();
		this.receiverMapping.put("latest_target_seen_proximity", "sentry");
		this.receiverMapping.put("latest_target_analyzed_proximity", "producer");
		this.receiverMapping.put("latest_target_produced_proximity", "carry");
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
		
//		ISpaceObject[] targets = appSpace.getSpaceObjectsByType("target");
//		for (int i = 0; i <= targets.length - 1; i++) {
//			ISpaceObject target1 = targets[i];
//			Vector2Double pos1 = (Vector2Double) target1.getProperty("position");
//			ISpaceObject target2 = targets[i+1];
//			Vector2Double pos2 = (Vector2Double) target2.getProperty("position");
//			
//			System.out.println("Distance: " + pos1.getDistance(pos2));
//		}
		
		applicationInterpreter.scheduleStep(new SendStep(ci));
	}

	private class SendStep implements IComponentStep<Void> {

		private CoordinationInfo ci = null;

		public SendStep(CoordinationInfo ci) {
			this.ci = ci;
		}

		@Override
		public IFuture<Void> execute(IInternalAccess ia) {
			String realization = (String) ci.getValueByName("dmlRealizationName");
			final String receiverType = receiverMapping.get(realization);
			// extract the space object from the coordination info
			CoordinationSpaceData data = (CoordinationSpaceData) ci.getValueByName("value");
			// System.out.println("###ProximityMechanism received: " + sp);
			// get the position
			IVector2 pos = new Vector2Double(data.getX(), data.getY());
			// get the max distance as specified by the mechanism configuration
			IVector1 distance = new Vector1Double(getMechanismConfiguration().getDoubleProperty("proximity"));
			// get all agents within the proximity
			@SuppressWarnings("unchecked")
			Set<ISpaceObject> nearAgents = appSpace.getNearObjects(pos, distance, new IFilter<ISpaceObject>() {

				@Override
				public boolean filter(ISpaceObject spaceObj) {
					if (spaceObj.getType().equals(receiverType)) {
						return true;
					}
					return false;
				}
			});
			if (nearAgents.isEmpty()) {
				applicationInterpreter.waitForDelay(1000, this);
			} else {
				// build the receiver list for the coordination space
				List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
				for (ISpaceObject agent : nearAgents) {
					IComponentDescription componentDescription = (IComponentDescription) agent.getProperty("owner");
					receiver.add(componentDescription);
				}
				
				// calculate the energy costs
				energyCosts.setProperty("costs", (Double) energyCosts.getProperty("costs") + distance.getAsDouble());

				space.publishCoordinationEvent(ci, receiver, getRealisationName(), ++eventNumber);
			}
			return IFuture.DONE;
		}
	}
}