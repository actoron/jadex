package sodekovs.marsworld.coordination;

import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IFilter;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector1Double;
import jadex.kernelbase.StatelessAbstractInterpreter;

import java.util.ArrayList;
import java.util.List;
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

	public ProximityMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		
		this.eventNumber = 0;
	}

	@Override
	public void start() {
		// nothing to be done here		
	}

	@Override
	public void stop() {
		// nothing to be done here		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void perceiveCoordinationEvent(Object obj) {
		CoordinationInfo ci = (CoordinationInfo) obj;
		// extract the space object from the coordination info
		ISpaceObject sp = (ISpaceObject) ci.getValueByName("value");
//		System.out.println("###ProximityMechanism received: " + sp);		
		// get the position
		IVector2 pos = (IVector2) sp.getProperty("position");
		// get the max distance as specified by the mechanism configuration
		IVector1 distance = new Vector1Double(getMechanismConfiguration().getDoubleProperty("proximity"));
		// get all agents within the proximity
		Set<ISpaceObject> nearAgents = appSpace.getNearObjects(pos, distance, new IFilter<ISpaceObject>() {

			@Override
			public boolean filter(ISpaceObject spaceObj) {
				if (spaceObj.getType().equals("sentry") || spaceObj.getType().equals("producer") || spaceObj.getType().equals("carry")) {
					return true;
				}
				return false;
			}
		});
		
		// build the receiver list for the coordination space
		List<IComponentDescription> receiver = new ArrayList<IComponentDescription>();
		for (ISpaceObject agent : nearAgents) {
			IComponentDescription componentDescription = (IComponentDescription) agent.getProperty("owner");
			receiver.add(componentDescription);
		}		
		
		space.publishCoordinationEvent(obj, receiver, getRealisationName(), ++eventNumber);
	}
}