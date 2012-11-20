package sodekovs.marsworld.coordination;

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
		
	/** The number of published events */
	protected Integer eventNumber = null;
	
	public BroadcastMechanism(CoordinationSpace space) {
		super(space);

		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();
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
		// just publish the coordination information to the coordination space, no computation is needed, he knows what to do with it
		space.publishCoordinationEvent(obj);
		++eventNumber;
	}
}