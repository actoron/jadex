/**
 * 
 */
package deco4mas.distributed.mechanism.convergence;

import java.util.HashMap;

import jadex.kernelbase.StatelessAbstractInterpreter;
import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import deco4mas.distributed.mechanism.CoordinationMechanism;

/**
 * @author thomas
 * 
 */
public class ConvergenceMechanism extends CoordinationMechanism {

	/** The applications interpreter */
	protected StatelessAbstractInterpreter applicationInterpreter = null;

	protected Adaption adaption = null;

	protected Integer answers = null;

	protected Double quorum = null;

	protected Long decisionTimeout = null;

	protected Long decisionDelay = null;

	protected Boolean blockMedium = null;

	protected Boolean resetInitiator = null;

	public ConvergenceMechanism(CoordinationSpace space) {
		super(space);

		// TODO Der Cast ist ein Hack bis Lars und Alex die Schnittstellen von Jadex anpassen
		this.applicationInterpreter = (StatelessAbstractInterpreter) space.getApplicationInternalAccess();

		// If it's a distributed application, then it has a contextID.
		HashMap<String, Object> appArgs = (HashMap<String, Object>) this.applicationInterpreter.getArguments();
		this.coordinationContextID = (String) appArgs.get("CoordinationContextID");
	}

	@Override
	public void start() {
		try {
			this.adaption = Adaption.parseAdaption(mechanismConfiguration.getProperty("ADAPTION"));
			this.answers = mechanismConfiguration.getIntegerProperty("ANSWERS");
			this.quorum = mechanismConfiguration.getDoubleProperty("QUORUM");
			this.decisionTimeout = mechanismConfiguration.getLongProperty("DECISION_TIMEOUT");
			this.decisionDelay = mechanismConfiguration.getLongProperty("DECISION_DELAY");
			this.blockMedium = mechanismConfiguration.getBooleanProperty("BLOCK_MEDIUM");
			this.resetInitiator = mechanismConfiguration.getBooleanProperty("RESET_INITIATOR");
		} catch (AdaptionParseException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see deco4mas.distributed.mechanism.ICoordinationMechanism#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see deco4mas.distributed.mechanism.ICoordinationMechanism#perceiveCoordinationEvent(java.lang.Object)
	 */
	@Override
	public void perceiveCoordinationEvent(Object obj) {
		// TODO Auto-generated method stub

	}

}
