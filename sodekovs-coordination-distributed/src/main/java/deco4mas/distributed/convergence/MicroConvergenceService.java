/**
 * 
 */
package deco4mas.distributed.convergence;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import deco.distributed.lang.dynamics.convergence.Adaption;
import deco.distributed.lang.dynamics.convergence.Convergence;

/**
 * @author thomas
 *
 */
public class MicroConvergenceService extends ConvergenceService {
	
	private MicroAgent agent = null;
	
	public MicroConvergenceService(MicroAgent agent, Convergence convergence, String coordinationContextId) {
		this.agent = agent;
		this.convergence = convergence;
		this.coordinationContextId = coordinationContextId;
	}

	@Override
	protected void initListener() {
			
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