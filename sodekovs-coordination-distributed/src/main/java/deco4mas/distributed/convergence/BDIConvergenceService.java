/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.AgentEvent;
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

import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Constraint;
import deco.distributed.lang.dynamics.convergence.Convergence;
import deco.distributed.lang.dynamics.convergence.Entry;
import deco.distributed.lang.dynamics.convergence.Realization;
import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;

/**
 * Implementing class for {@link IConvergenceService}.
 * 
 * @author Thomas Preisler
 */
@Service
public class BDIConvergenceService extends ConvergenceService {

	public static final String BDI_BELIEF = "BDI_BELIEF";

	private ExternalAccessFlyweight externalAccess = null;

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
						base.getBelief(constraint.getElement()).addBeliefListener(new BDIBeliefListener(adaption, constraint));
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

	private void adapt(final Adaption adaption) {
		SServiceProvider.getServices(externalAccess.getServiceProvider(), ICoordinationSpaceService.class, RequiredServiceInfo.SCOPE_GLOBAL).addResultListener(
				new IntermediateDefaultResultListener<ICoordinationSpaceService>() {

					@Override
					public void intermediateResultAvailable(ICoordinationSpaceService service) {
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

	private class BDIBeliefListener implements IBeliefListener {

		private Adaption adaption = null;
		private Constraint constraint = null;

		private BDIBeliefListener(Adaption adaption, Constraint constraint) {
			this.adaption = adaption;
			this.constraint = constraint;
		}

		@Override
		public void beliefChanged(AgentEvent ae) {
			// for now we just assume it is an integer value
			Integer value = (Integer) ae.getValue();

			// also we assume that the condition is fulfilled if the condition value (integer) equals the current belief value
			if (constraint.getCondition().equals(value)) {
				// initialize the voting result listener
				final VoteResultListener voteResultListener = new VoteResultListener(adaption);
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
						IFuture<Boolean> res = service.vote(adaption);
						res.addResultListener(voteResultListener);
					}
				});
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private class VoteResultListener extends DefaultResultListener {

		private List<Boolean> results = new ArrayList<Boolean>();
		private Adaption adaption = null;
		private boolean finished = false;

		private VoteResultListener(Adaption adaption) {
			this.adaption = adaption;
		}

		public void setFinished() {
			finished = true;
			evaluate();
		}

		@Override
		public void resultAvailable(Object result) {
			if (!finished) {
				results.add((Boolean) result);
				if (results.size() == adaption.getAnswer()) {
					evaluate();
				}
			}
		}

		private void evaluate() {
			Integer yes = 0, no = 0;
			for (Boolean result : results) {
				if ((java.lang.Boolean) result) {
					yes++;
				} else {
					no++;
				}
			}
			Double voteResult = new Double(yes) / new Double(yes + no);

			if (voteResult >= adaption.getQuorum()) {
				adapt(adaption);
			}
		}
	}
}