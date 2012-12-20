/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.SServiceProvider;
import jadex.commons.IFilter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;

import java.util.ArrayList;
import java.util.List;

import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Constraint;
import deco.distributed.lang.dynamics.convergence.Convergence;
import deco.distributed.lang.dynamics.convergence.Entry;
import deco.distributed.lang.dynamics.convergence.Realization;
import deco4mas.distributed.coordinate.service.ICoordinationSpaceService;

/**
 * This class implements the {@link IConvergenceService} for {@link ConvergenceMicroAgent}s by implementing the interface methods and adding listeners to the agent to monitor if a distributed voting
 * process should be started.
 * 
 * @author Thomas Preisler
 */
@Service
public class MicroConvergenceService extends ConvergenceService {

	/** Reference to the mirco agent */
	private ConvergenceMicroAgent agent = null;

	public MicroConvergenceService(ConvergenceMicroAgent agent, Convergence convergence, String coordinationContextId) {
		this.agent = agent;
		this.convergence = convergence;
		this.coordinationContextId = coordinationContextId;

		System.out.println("ComponentIdentifier: " + agent.getComponentIdentifier() + "  HashCode: " + agent.getComponentIdentifier().hashCode());
	}

	@Override
	protected void initListener() {
		// get all affected adaptions
		List<Adaption> adaptions = convergence.getAffectedAdaptions(agent.getExternalAccess().getLocalType());
		for (Adaption adaption : adaptions) {
			// get all matching constraints
			List<Constraint> constraints = adaption.getConstraints(agent.getExternalAccess().getLocalType());
			for (Constraint constraint : constraints) {
				if (constraint.getType().equals(MICRO_CONSTRAINT)) {
					agent.addEventListener(constraint.getElement(), new MicroAgentConvergenceListener(adaption, constraint));
					System.out.println(agent.getComponentIdentifier() + " initialized listener for " + constraint);
				}
			}
		}
	}

	@Override
	protected void startService() {
		String serviceName = agent.getComponentIdentifier() + SERVICE_POSTFIX;
		agent.addService(serviceName, IConvergenceService.class, this);
		System.out.println(serviceName + " started");
	}

	@Override
	public void resetConstraint(Adaption adaption) {
		// / get all matching constraints
		List<Constraint> constraints = adaption.getConstraints(agent.getExternalAccess().getLocalType());
		for (final Constraint constraint : constraints) {
			if (constraint.getType().equals(MICRO_CONSTRAINT)) {
				agent.setConstraintValue(constraint.getElement(), new Integer(0));
			}
		}
	}

	@Override
	public IFuture<Boolean> vote(Adaption adaption, IComponentIdentifier initiator) {
		System.out.println(agent.getComponentIdentifier() + " vote is called for " + adaption + " by " + initiator);
		boolean result = true;

		// if the service himself already started the same adaption
		if (runningAdaptions.containsKey(adaption)) {
			int ownHashCode = agent.getComponentIdentifier().hashCode();
			int remoteHashCode = initiator.hashCode();

			// abort the adaption if he remote initiators component identifier has a higher hash value
			if (remoteHashCode > ownHashCode) {
				runningAdaptions.put(adaption, false);
				// else if your hash code is higher vote false
			} else if (ownHashCode > remoteHashCode) {
				result = false;
			}
		}

		Future<Boolean> fut = new Future<Boolean>();

		// get all constraints which reference the agent type
		List<Constraint> constraints = adaption.getConstraints(agent.getExternalAccess().getLocalType());
		for (Constraint constraint : constraints) {
			Integer value = (Integer) agent.getConstraintValue(constraint.getElement());

			result = result && value != null && value >= constraint.getThreshold();
		}

		System.out.println(agent.getComponentIdentifier().getName() + " voted " + result);
		fut.setResult(result);

		return fut;
	}

	private class MicroAgentConvergenceListener implements IMicroAgentConvergenceListener {

		private Adaption adaption = null;
		private Constraint constraint = null;
		/** Was there a previous voting attempt */
		private boolean previous = false;
		/** Timestamp of the last voting attempt */
		private long lastCall = 0;

		private MicroAgentConvergenceListener(Adaption adaption, Constraint constraint) {
			this.adaption = adaption;
			this.constraint = constraint;
		}

		@Override
		public void constraintChangend(final ConstraintChangeEvent event) {
			// if there was a previous voting attempt wait for the specified delay before checking the constraint again
			if (previous) {
				// calculate delay
				long currentCall = System.currentTimeMillis();
				long delayTemp = adaption.getDelay() - (currentCall - lastCall);
				final long delay = delayTemp >= 0 ? delayTemp : 0;
				// and wait
				agent.scheduleStep(new IComponentStep<Void>() {

					@Override
					public IFuture<Void> execute(IInternalAccess ia) {
						return ia.waitForDelay(delay, new IComponentStep<Void>() {

							@Override
							public IFuture<Void> execute(IInternalAccess ia) {
								checkConstraint(event);
								return IFuture.DONE;
							}
						});
					}

				});
			} else {
				checkConstraint(event);
			}
		}

		private void checkConstraint(ConstraintChangeEvent event) {
			previous = false;
			// for now we just assume it is an integer value
			final Integer value = (Integer) event.getValue();
			// also we assume that the condition is fulfilled if the condition value (integer) equals the current belief value
			if (value >= constraint.getCondition()) {
				previous = true;
				System.out.println(agent.getComponentIdentifier() + " constraint fulfilled " + constraint);
				// initialize the voting result listener
				final VoteResultListener voteResultListener = new VoteResultListener(adaption, event.getConstraint());
				// mark this adaption as started and running
				runningAdaptions.put(adaption, true);
				// start the voting timeout which will notify the voting result listener
				agent.scheduleStep(new IComponentStep<Void>() {

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
				IIntermediateFuture<IConvergenceService> result = SServiceProvider.getServices(agent.getServiceProvider(), IConvergenceService.class, RequiredServiceInfo.SCOPE_GLOBAL,
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
						IFuture<Boolean> res = service.vote(adaption, agent.getComponentIdentifier());
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
		/** Reference of constraint key which is referenced by the constraint responsible for this adaption */
		private String constraintKey = null;

		/**
		 * Constructor.
		 * 
		 * @param adaption
		 *            the given {@link Adaption}
		 * @param constraintKey
		 *            the referenced constraint key
		 */
		private VoteResultListener(Adaption adaption, String constraintKey) {
			this.adaption = adaption;
			this.constraintKey = constraintKey;
		}

		/**
		 * This method is called when a voting timeout occurs.
		 */
		public void setFinished() {
			if (!finished) {
				System.out.println(agent.getComponentIdentifier() + " voting timeout");
				// set it finished
				finished = true;
				// and evaluate the results
				evaluate();
			}
		}

		@Override
		public void resultAvailable(Object result) {
			// only if the vote is not finished yet results are accepted
			if (!finished) {
				System.out.println(agent.getComponentIdentifier() + " received voting result " + result);
				results.add((Boolean) result);
				// if all required results are received
				if (results.size() == adaption.getAnswer()) {
					System.out.println(agent.getComponentIdentifier() + " received required number of answers " + adaption.getAnswer());
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
			// check if the adaption was not aborted because of a remote adaption with a higher priority (remote initiators component identifier)
			if (runningAdaptions.get(adaption)) {
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
					System.out.println(agent.getComponentIdentifier() + " quorom is reached " + voteResult);
					// adapt!
					adapt(adaption);
					// reset
					resetConstraints(adaption);
					// if not check for reset
				} else {
					System.out.println(agent.getComponentIdentifier() + " quorom is was not reached " + voteResult);
					if (adaption.getReset()) {
						// for now reseting just means to set the integer value back to 0
						agent.setConstraintValue(constraintKey, new Integer(0));
					}
				}
			} else {
				System.out.println(agent.getComponentIdentifier() + " adaption was aborted due to remote one with higher priority");
			}
		}
	}

	/**
	 * Proceeds an adaption by calling all {@link ICoordinationSpaceService}s and with the according changes.
	 * 
	 * @param adaption
	 *            The given {@link Adaption}
	 */
	private void adapt(final Adaption adaption) {
		System.out.println(agent.getComponentIdentifier() + " starts adaption process for " + adaption);
		// get all other coordination spaces for the given coordination context
		SServiceProvider.getServices(agent.getServiceProvider(), ICoordinationSpaceService.class, RequiredServiceInfo.SCOPE_GLOBAL, new IFilter<ICoordinationSpaceService>() {

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
	 * Calls all remote convergence services and ask them to reset their convergence constraints if a previous voting attempt (Adaption) was successfull.
	 * 
	 * @param adaption
	 *            the given {@link Adaption}
	 */
	protected void resetConstraints(final Adaption adaption) {
		System.out.println(agent.getComponentIdentifier() + " resets all constraints for " + adaption);
		// get remote convergence services
		SServiceProvider.getServices(agent.getServiceProvider(), IConvergenceService.class, RequiredServiceInfo.SCOPE_GLOBAL, new IFilter<IConvergenceService>() {

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