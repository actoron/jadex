package deco4mas.examples.agentNegotiation.deco.media;

import jadex.bridge.service.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import deco4mas.coordinate.environment.CoordinationSpace;
import deco4mas.mechanism.ICoordinationMechanism;

/**
 * Process for check up deadline at medium
 */
public class MediumTimeProcess extends SimplePropertyObject implements ISpaceProcess {
	/**
	 * This method will be executed by the object before the process gets added to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space) {
		System.out.println("#Start MediumTimeProcess");
	}

	/**
	 * This method will be executed by the object before the process is removed from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space) {
		// System.out.println("#mediumTimeProcess");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space) {
		CoordinationSpace env = (CoordinationSpace) space;
		if (env.getActiveCoordinationMechanisms().size() > 0) {
			for (ICoordinationMechanism coordMechanism : env.getActiveCoordinationMechanisms()) {
				if (coordMechanism.getRealisationName().equals("by_neg")) {
					NegotiationMechanism mechanism = (NegotiationMechanism) coordMechanism;
					mechanism.nextTick();
				}
			}
		}
	}
}
