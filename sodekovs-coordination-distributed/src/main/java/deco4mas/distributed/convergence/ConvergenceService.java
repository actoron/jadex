package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Convergence;

/**
 * Abstract superclass for all classes implementing the {@link IConvergenceService}.
 * 
 * @author Thomas Preisler
 */
public abstract class ConvergenceService implements IConvergenceService {
	
	/** BDI Belief Constant */
	public static final String BDI_BELIEF = "BDI_BELIEF";
	
	public static final String MICRO_CONSTRAINT = "MICRO_CONSTRAINT";
	
	/** Postfix of the service name */
	protected static final String SERVICE_POSTFIX = "ConvergenceService";
	
	/** Convergence object form MASDynamics */
	protected Convergence convergence = null;
	
	/** The coordination context */
	protected String coordinationContextId = null;
	
	/** Map of currently running {@link Adaption}s*/
	protected Map<Adaption, Boolean> runningAdaptions = null;
	
	/** List of successful adaptations */
	protected List<Adaption> successfulAdaptions = null;
	
	/**
	 * Default constructor.
	 */
	public ConvergenceService() {
		this.runningAdaptions = new HashMap<Adaption, Boolean>();
		this.successfulAdaptions = new ArrayList<Adaption>();
	}
	
	@Override
	public abstract IFuture<Boolean> vote(Adaption adaption, IComponentIdentifier initiator);
	
	@Override
	public String getCoordinationContextID() {
		return this.coordinationContextId;
	}
	
	/**
	 * Adds the service to the platform and starts it and initializes the convergence listeners.
	 */
	public void start() {
		startService();
		initListener();
	}

	/**
	 * Initializes the convergence listeners.
	 */
	protected abstract void initListener();

	/**
	 * Adds the service to the platform and starts it.
	 */
	protected abstract void startService();
	
	
}