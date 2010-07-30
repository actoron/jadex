package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceContract;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * sign end
 */
public class AchieveContractPlan extends Plan
{

	public void body()
	{
		final Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());
		IInternalEvent offerEvent = (IInternalEvent) getReason();
		ServiceContract offer = (ServiceContract) offerEvent.getParameter("contract").getValue();
		saLogger.info("contract sealed with");
		getBeliefbase().getBelief("contract").setFact(offer);
	}
}
