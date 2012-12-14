/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;

import java.util.List;

import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Constraint;
import deco.distributed.lang.dynamics.convergence.Convergence;

/**
 * @author thomas
 *
 */
@Service
public class MicroConvergenceService extends ConvergenceService {
	
	private MicroAgent agent = null;
	
	public MicroConvergenceService(MicroAgent agent, Convergence convergence, String coordinationContextId) {
		this.agent = agent;
		this.convergence = convergence;
		this.coordinationContextId = coordinationContextId;
	}

	@Override
	protected void initListener() {
		//TODO Get one of the agents parameters
		Class<? extends MicroAgent> clazz = agent.getClass();
		// get all affected adaptions
		List<Adaption> adaptions = convergence.getAffectedAdaptions(agent.getAgentName());
		for (Adaption adaption : adaptions) {
			// get all matching constraints
			List<Constraint> constraints = adaption.getConstraints(agent.getAgentName());
			for (Constraint constraint : constraints) {
				if (constraint.getType().equals(MICRO_PARAMETER)) {
					//TODO get the parameter
				}
			}
		}
	}
	
	@Override
	protected void startService() {
		String serviceName = agent.getComponentIdentifier().getLocalName() + SERVICE_POSTFIX;
		agent.addService(serviceName, IConvergenceService.class, this);
	}

	@Override
	public void resetConstraint(Adaption adaption) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public IFuture<Boolean> vote(Adaption adaption, IComponentIdentifier initiator) {
		// TODO Auto-generated method stub
		return null;
	}
}