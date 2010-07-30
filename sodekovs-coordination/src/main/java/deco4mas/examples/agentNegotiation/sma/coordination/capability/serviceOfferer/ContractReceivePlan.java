package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequestedService;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationContractInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Receive a Reward
 */
public class ContractReceivePlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		try
		{
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			IInternalEvent rewardEvent = (IInternalEvent) getReason();
			NegotiationContractInformation info = (NegotiationContractInformation) rewardEvent.getParameter("information").getValue();
			if ((info.getContract().getInitiator().getName()).equals(this.getComponentIdentifier().getName()))
			{
				for (RequestedService requested : (RequestedService[]) getBeliefbase().getBeliefSet("requestedServices").getFacts())
				{
					if (info.getContract().getServiceType().getName().equals(requested.getServiceType().getName()))

						synchronized (requested)
						{
							if (info.getState().equals(NegotiationContractInformation.FINAL_REWARD))
							{
								// LOG
								System.out.println(this.getComponentName() + " accept " + info);
								smaLogger.info("signed " + info);

								requested.setSa(info.getContract().getParticipant());
								getBeliefbase().getBeliefSet("requestedServices").removeFact(requested);

								ValueLogger.addValue("sign_" + info.getContract().getParticipant().getLocalName(), 1.0);

								// test if the workflow can be executed
								IInternalEvent satisfy = createInternalEvent("serviceSatisfied");
								satisfy.getParameter("contract").setValue(info.getContract());
								dispatchInternalEvent(satisfy);
							} else
							{
								if (requested.getState().equals(RequestedService.SEARCHING))
								{
									smaLogger.info("Designated Sa " + info.getContract().getParticipant());
									requested.setDesignatedSa(true);
									info.setAnswer(true, this.getComponentIdentifier());
								} else
								{
									smaLogger.info("Designated Sa rejected " + info.getContract().getParticipant());
									info.setAnswer(false, this.getComponentIdentifier());
								}
								IInternalEvent reply = createInternalEvent("negotiationContractReply");
								reply.getParameter("information").setValue(info);
								dispatchInternalEvent(reply);
							}
						}
				}
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}
}
