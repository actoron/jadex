/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBelief;
import jadex.bdi.runtime.IBeliefListener;
import jadex.bdi.runtime.IBeliefbase;
import jadex.bdi.runtime.impl.flyweights.BeliefbaseFlyweight;
import jadex.bdi.runtime.impl.flyweights.ExternalAccessFlyweight;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IFilter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

import deco.distributed.lang.dynamics.MASDynamics;
import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Constraint;
import deco.distributed.lang.dynamics.convergence.Convergence;
import deco.distributed.lang.dynamics.convergence.Entry;
import deco.distributed.lang.dynamics.convergence.Realization;
import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;

/**
 * This class implements the {@link IConvergenceService} for BDI agents by implementing the interface methods and adding listeners to the agent to monitor if a distributed voting process should be
 * started.
 * 
 * @author Thomas Preisler
 */
// TODO Handle delay
@Service
public class BDIConvergenceService extends ConvergenceService {

	/** BDI Belief Constant */
	public static final String BDI_BELIEF = "BDI_BELIEF";

	/** External access for the agent */
	private ExternalAccessFlyweight externalAccess = null;

	/**
	 * Constructor.
	 * 
	 * @param externalAccess
	 *            the external access for the agent
	 * @param convergence
	 *            the {@link Convergence} extracted form the {@link MASDynamics}
	 * @param coordinationContextId
	 *            the coordination context id
	 */
	public BDIConvergenceService(ExternalAccessFlyweight externalAccess, Convergence convergence, String coordinationContextId) {
		this.externalAccess = externalAccess;
		this.convergence = convergence;
		this.coordinationContextId = coordinationContextId;
	}

	@Override
	public IFuture<Boolean> vote(Adaption adaption) {
		Future<Boolean> fut = new Future<Boolean>();
		boolean result = true;

		// get all constraints which reference the agent type
		List<Constraint> constraints = adaption.getConstraints(externalAccess.getLocalType());
		for (Constraint constraint : constraints) {
			// get the belief value
			IOAVState state = externalAccess.getState();
			Object[] scope = AgentRules.resolveCapability(constraint.getElement(), OAVBDIMetaModel.belief_type, externalAccess.getScope(), state);
			Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);

			if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0])) {
				IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
				// for now we just assume it is an integer value
				Integer value = (Integer) base.getBelief(constraint.getElement()).getFact();

				// result should only be true if all referenced constraints are fulfilled
				result = result && value >= constraint.getThreshold();
			}
		}

		fut.setResult(result);

		return fut;
	}

	@Override
	protected void initListener() {
		// get all affected adaptions
		List<Adaption> adaptions = convergence.getAffectedAdaptions(externalAccess.getLocalType());
		for (Adaption adaption : adaptions) {
			IOAVState state = externalAccess.getState();

			// get all matching constraints
			List<Constraint> constraints = adaption.getConstraints(externalAccess.getLocalType());
			for (Constraint constraint : constraints) {
				if (constraint.getType().equals(BDI_BELIEF)) {
					Object[] scope = AgentRules.resolveCapability(constraint.getElement(), OAVBDIMetaModel.belief_type, externalAccess.getScope(), state);
					Object mscope = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);

					if (state.containsKey(mscope, OAVBDIMetaModel.capability_has_beliefs, scope[0])) {
						IBeliefbase base = BeliefbaseFlyweight.getBeliefbaseFlyweight(state, scope[1]);
						// initialize listener for adaption and constraint
						IBelief belief = base.getBelief(constraint.getElement());
						belief.addBeliefListener(new BDIBeliefListener(adaption, constraint, belief));
					}
				}
			}
		}
	}

	@Override
	protected void startService() {
		String serviceName = externalAccess.getComponentIdentifier().getLocalName() + SERVICE_POSTFIX;
		externalAccess.getInterpreter().addService(serviceName, IConvergenceService.class, BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, this, null);
	}

	/**
	 * Proceeds an adaption by calling all {@link ICoordinationSpaceService}s and with the according changes.
	 * 
	 * @param adaption
	 *            The given {@link Adaption}
	 */
	private void adapt(final Adaption adaption) {
		// get all other coordination spaces for the given coordination context
		SServiceProvider.getServices(externalAccess.getServiceProvider(), ICoordinationSpaceService.class, RequiredServiceInfo.SCOPE_GLOBAL, new IFilter<ICoordinationSpaceService>() {

			@Override
			public boolean filter(ICoordinationSpaceService obj) {
				if (coordinationContextId.equals(obj.getCoordinationContextID())) {
					return true;
				}
				return false;
			}
		}).addResultListener(new IntermediateDefaultResultListener<ICoordinationSpaceService>() {

			@Override
			public void intermediateResultAvailable(ICoordinationSpaceService service) {
				// iterate over all realizations in the adaption
				for (Realization realization : adaption.getRealizations()) {
					if (realization.getActivate() != null) {
						if (realization.getActivate()) {
							// activate the mechanism
							service.activateCoordinationMechanism(realization.getId());
						} else {
							// deactivate the mechanism
							service.deactivateCoordinationMechanism(realization.getId());
						}
					} else {
						for (Entry entry : realization.getEntries()) {
							// change the mechanisms configuration
							service.changeCoordinationMechanismConfiguration(realization.getId(), entry.getKey(), entry.getValue());
						}
					}
				}
			}
		});
	}

	/**
	 * Private {@link IBeliefListener} class. Observes the agent for an given {@link Adaption}, {@link Constraint} and {@link IBelief}.
	 */
	private class BDIBeliefListener implements IBeliefListener {

		/** The given Adaption */
		private Adaption adaption = null;
		/** The given Constraint */
		private Constraint constraint = null;
		/** The Belief under observation */
		private IBelief belief = null;

		/**
		 * Constructor.
		 * 
		 * @param adaption
		 *            the given {@link Adaption}
		 * @param constraint
		 *            the given {@link Constraint}
		 * @param belief
		 *            the belief under obervation
		 */
		private BDIBeliefListener(Adaption adaption, Constraint constraint, IBelief belief) {
			this.adaption = adaption;
			this.constraint = constraint;
			this.belief = belief;
		}

		@Override
		public void beliefChanged(AgentEvent ae) {
			// for now we just assume it is an integer value
			Integer value = (Integer) ae.getValue();

			// also we assume that the condition is fulfilled if the condition value (integer) equals the current belief value
			if (constraint.getCondition() >= value) {
				// initialize the voting result listener
				final VoteResultListener voteResultListener = new VoteResultListener(adaption, belief);
				// start the voting timeout which will notify the voting result listener
				externalAccess.scheduleStep(new IComponentStep<Void>() {

					@Override
					public IFuture<Void> execute(IInternalAccess ia) {
						ia.waitForDelay(adaption.getTimeout(), new IComponentStep<Void>() {

							@Override
							public IFuture<Void> execute(IInternalAccess ia) {
								voteResultListener.setFinished();
								return IFuture.DONE;
							}
						});
						return IFuture.DONE;
					}
				});
				// get all other IConvergence services for the same coordination context
				IIntermediateFuture<IConvergenceService> result = SServiceProvider.getServices(externalAccess.getServiceProvider(), IConvergenceService.class, RequiredServiceInfo.SCOPE_GLOBAL,
						new IFilter<IConvergenceService>() {

							@Override
							public boolean filter(IConvergenceService obj) {
								if (obj.getCoordinationContextID().equals(coordinationContextId)) {
									return true;
								}
								return false;
							}
						});
				result.addResultListener(new IntermediateDefaultResultListener<IConvergenceService>() {

					@SuppressWarnings("unchecked")
					@Override
					public void intermediateResultAvailable(IConvergenceService service) {
						// let them vote
						IFuture<Boolean> res = service.vote(adaption);
						// and given the result a reference to the vote result listener
						res.addResultListener(voteResultListener);
					}
				});
			}
		}
	}

	/**
	 * The voting result listener. Is informed whenever a voting result occurs or the timeout is reached.
	 */
	@SuppressWarnings("rawtypes")
	private class VoteResultListener extends DefaultResultListener {

		/** {@link List} of all the voting results */
		private List<Boolean> results = new ArrayList<Boolean>();
		/** The according {@link Adaption} over which is voted */
		private Adaption adaption = null;
		/** Finished if the timeout runs out or all required answer have been received */
		private boolean finished = false;
		/** Reference of the belief which is referenced by the constraint responsible for this adaption */
		private IBelief belief = null;

		/**
		 * Constructor.
		 * 
		 * @param adaption
		 *            the given {@link Adaption}
		 * @param belief
		 *            the referenced {@link IBelief}
		 */
		private VoteResultListener(Adaption adaption, IBelief belief) {
			this.adaption = adaption;
			this.belief = belief;
		}

		/**
		 * This method is called when a voting timeout occurs.
		 */
		public void setFinished() {
			// set it finished
			finished = true;
			// and evaluate the results
			evaluate();
		}

		@Override
		public void resultAvailable(Object result) {
			// only if the vote is not finished yet results are accepted
			if (!finished) {
				results.add((Boolean) result);
				// if all required results are received
				if (results.size() == adaption.getAnswer()) {
					finished = true;
					// evaluate
					evaluate();
				}
			}
		}

		/**
		 * Evaluates the received voting results.
		 */
		private void evaluate() {
			Integer yes = 0, no = 0;
			// count number of yes and no votes
			for (Boolean result : results) {
				if ((java.lang.Boolean) result) {
					yes++;
				} else {
					no++;
				}
			}
			// calculate the result
			Double voteResult = new Double(yes) / new Double(yes + no);

			// if the quorum is reached
			if (voteResult >= adaption.getQuorum()) {
				// adapt!
				adapt(adaption);
				// if not check for reset
			} else if (adaption.getReset()) {
				// for now reseting just means to set the integer value back to 0
				belief.setFact(new Integer(0));
			}
		}
	}
}