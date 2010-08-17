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
	public void body()
	{
		try
		{
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			IInternalEvent rewardEvent = (IInternalEvent) getReason();
			NegotiationContractInformation info = (NegotiationContractInformation) rewardEvent.getParameter("information").getValue();
			//find request
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
								smaLogger.info("signed " + info);

								System.out.println("#acceptFinal(Sma)" + info);
								requested.setSa(info.getContract().getParticipant());
								getBeliefbase().getBeliefSet("requestedServices").removeFact(requested);

								ValueLogger.addValue("sign_" + info.getContract().getParticipant().getLocalName(), 1.0);

								// test if the workflow can be executed
								IInternalEvent satisfy = createInternalEvent("serviceSatisfied");
								satisfy.getParameter("contract").setValue(info.getContract());
								dispatchInternalEvent(satisfy);
							} else if (info.getState().equals(NegotiationContractInformation.TENTATIVE_REWARD))
							{
								if (requested.getState().equals(RequestedService.SEARCHING))
								{
									System.out.println("#acceptReward(Sma)" + info);
									smaLogger.info("Designated Sa: " + info);
									requested.setDesignatedSa(true);
									info.setAnswer(true, this.getComponentIdentifier());
								} else
								{
									System.out.println("#rejectReward(Sma)" + info);
									smaLogger.info("Designated Sa rejected " + info.getContract().getParticipant());
									info.setAnswer(false, this.getComponentIdentifier());
								}
								IInternalEvent reply = createInternalEvent("negotiationContractReply");
								reply.getParameter("information").setValue(info);
								dispatchInternalEvent(reply);
							} else if (info.getState().equals(NegotiationContractInformation.CANCELED_REWARD))
							{
								System.out.println("#resetDesignated(Sma)" + info);
								smaLogger.info("Reset Designated Sa: " + info);
								requested.setDesignatedSa(false);
								info.setAnswer(true, this.getComponentIdentifier());
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
