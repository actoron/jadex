package deco4mas.examples.agentNegotiation.sa.coordination.capability.serviceSupplier;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.negotiationInformation.NegotiationContractInformation;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

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
			Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

			IInternalEvent contractEvent = (IInternalEvent) getReason();
			NegotiationContractInformation info = (NegotiationContractInformation) contractEvent.getParameter("information").getValue();
			if (info.getContract().getParticipant().getName().equals(this.getComponentIdentifier().getName()))
			{
				synchronized ((Boolean) getBeliefbase().getBelief("signed").getFact())
				{
					if ((Boolean) getBeliefbase().getBelief("signed").getFact()
						&& (Boolean) getBeliefbase().getBelief("blackout").getFact())
					{
						saLogger.info("Decline Reward(" + info.getId() + ")" + ", allready signed or blackout");

					} else
					{
						if (info.getState().equals(NegotiationContractInformation.FINAL_REWARD))
						{
							System.out.println("#finalReward(Sa) " + info);
							saLogger.info("Final Reward " + info);

							getBeliefbase().getBelief("signed").setFact(Boolean.TRUE);
							IInternalEvent sealed = createInternalEvent("contractSealed");
							sealed.getParameter("contract").setValue(info.getContract());
							dispatchInternalEvent(sealed);
						} else
						{
							info.setAnswer(Boolean.TRUE, this.getComponentIdentifier());
							System.out.println("#acceptReward(Sa) " + info);
							saLogger.info("Accept Reward " + info);

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
