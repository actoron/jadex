package deco4mas.examples.agentNegotiation.sa;

import deco4mas.examples.agentNegotiation.ServiceType;
import jadex.bdi.runtime.Plan;

/**
 * Execute a service
 */
public class ExecuteServicePlan extends Plan
{
	public void body()
	{
		ServiceType myService = (ServiceType) getBeliefbase().getBelief("providedService").getFact();
		AgentType agentType = (AgentType) getBeliefbase().getBelief("agentType").getFact();
		
		Double duration = myService.getMedDuration() * (0.5 + agentType.getCostCharacter());
		waitFor(duration.longValue());
		System.out.println(getComponentName() + ": " +  duration + " EXECUTED!");
//		System.out.println();

		getParameter("result").setValue(Boolean.TRUE);
//		getParameter("result").setValue(Boolean.FALSE);
	}
}
