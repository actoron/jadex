package deco4mas.examples.agentNegotiation.sa.application;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.Contract;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.ServiceOffer;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * sign end
 */
public class ContractSealedPlan extends Plan
{

	public void body()
	{
		final Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());
		IInternalEvent offerEvent = (IInternalEvent) getReason();
		Contract offer = (Contract) offerEvent.getParameter("contract").getValue();
		saLogger.info("contract sealed with");
		getBeliefbase().getBelief("contract").setFact(offer);
	}
}
