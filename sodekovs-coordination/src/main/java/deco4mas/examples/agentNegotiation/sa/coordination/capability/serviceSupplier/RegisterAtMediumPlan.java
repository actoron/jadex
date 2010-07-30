package deco4mas.examples.agentNegotiation.sa.coordination.capability.serviceSupplier;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceAgentType;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceType;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.DirectNegotiationParticipantInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sa.coordination.negotiationstrategy.EasyBidStrategy;

/**
 * Register sa on negotiation medium(s)
 */
public class RegisterAtMediumPlan extends Plan
{
	private static Integer id = 0;

	public void body()
	{
		try
		{
			final Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

			EasyBidStrategy strategy = new EasyBidStrategy(this.getComponentIdentifier(), (ServiceType) getBeliefbase().getBelief(
				"providedService").getFact(), (ServiceAgentType) getBeliefbase().getBelief("serviceAgentType").getFact());
			DirectNegotiationParticipantInformation info = new DirectNegotiationParticipantInformation(id, this.getComponentIdentifier(),
				(ServiceType) getBeliefbase().getBelief("providedService").getFact(), strategy, this.getExternalAccess());
			saLogger.info("register: " + info);
			System.out.println(this.getComponentName() + " register " + info);
			IInternalEvent register = createInternalEvent("mediumRegister");
			register.getParameter("information").setValue(info);
			dispatchInternalEvent(register);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
