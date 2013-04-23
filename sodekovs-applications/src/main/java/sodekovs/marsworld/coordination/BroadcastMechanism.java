package sodekovs.marsworld.coordination;

import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.kernelbase.StatelessAbstractInterpreter;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * A broadcast coordination medium which broadcasts coordination information to all applicable agents.
 * 
 * @author Thomas Preisler
 */
public class BroadcastMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;
	
	/** The application environment used for proximity calculation */
	protected ContinuousSpace2D appSpace = null;
		
	/** The number of published events */
	protected Integer eventNumber = null;
	
	protected ISpaceObject energyCosts = null;
	
	public BroadcastMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
		this.appSpace = (ContinuousSpace2D) applicationInterpreter.getExtension("my2dspace");
		this.energyCosts = appSpace.getSpaceObjectsByType("energyCosts")[0];
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

	@Override
	public void perceiveCoordinationEvent(Object obj) {
		// calculate the energy costs
		double factor = (Double) energyCosts.getProperty("factor");
		double oldCosts = (Double) energyCosts.getProperty("costs");
		double costs = oldCosts + (Math.sqrt(2.0) * factor);
		// and store them
		energyCosts.setProperty("costs", costs);
		
		// just publish the coordination information to the coordination space, no computation is needed, he knows what to do with it
		space.publishCoordinationEvent(obj);
		++eventNumber;
	}
}