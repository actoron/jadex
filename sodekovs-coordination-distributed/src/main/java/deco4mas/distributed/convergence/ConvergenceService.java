package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IFilter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.util.HashMap;
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
	
	/** Map of currently runnig {@link Adaption}s*/
	protected Map<Adaption, Boolean> runningAdaptions = null;
	
	protected IExternalAccess externalAccess = null;
	
	/**
	 * Default constructor.
	 */
	public ConvergenceService() {
		this.runningAdaptions = new HashMap<Adaption, Boolean>();
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
	
	/**
	 * Calls all remote convergence services and ask them to reset their convergence constraints if a previous voting attempt (Adaption) was successfull.
	 * 
	 * @param adaption
	 *            the given {@link Adaption}
	 */
	protected void resetConstraints(final Adaption adaption) {
		System.out.println(externalAccess.getComponentIdentifier() + " resets all constraints for " + adaption);
		// get remote convergence services
		SServiceProvider.getServices(externalAccess.getServiceProvider(), IConvergenceService.class, RequiredServiceInfo.SCOPE_GLOBAL, new IFilter<IConvergenceService>() {

			@Override
			public boolean filter(IConvergenceService obj) {
				if (coordinationContextId.equals(obj.getCoordinationContextID())) {
					return true;
				}
				return false;
			}
		}).addResultListener(new IntermediateDefaultResultListener<IConvergenceService>() {

			@Override
			public void intermediateResultAvailable(IConvergenceService result) {
				// and reset them
				result.resetConstraint(adaption);
			}
		});
	}
}