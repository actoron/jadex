/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.commons.future.IFuture;
import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Convergence;

/**
 * @author thomas
 *
 */
public abstract class ConvergenceService implements IConvergenceService {
	
	protected static final String SERVICE_POSTFIX = "ConvergenceService";
	
	protected Convergence convergence = null;
	
	protected String coordinationContextId = null;

	@Override
	public abstract IFuture<Boolean> vote(Adaption adaption);
	
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