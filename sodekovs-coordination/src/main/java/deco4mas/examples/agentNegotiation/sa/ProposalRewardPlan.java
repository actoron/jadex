package deco4mas.examples.agentNegotiation.sa;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.deco.Reward;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Receive a Reward
 */
public class ProposalRewardPlan extends Plan
{
	/**
	 * The plan body.
	 */
	public void body()
	{
		Logger saLogger = AgentLogger.getTimeEvent(this.getComponentName());

		IGoal rewardGoal = (IGoal) getReason();
		Reward reward = (Reward) rewardGoal.getParameter("reward").getValue();
		if (reward.testPartizipant(this.getComponentIdentifier()))
		{
			startAtomic();
			Boolean signed = (Boolean) getBeliefbase().getBelief("signed").getFact();
			if (signed)
			{
				saLogger.info("Decline Reward(" + reward.getId() + ")" + ", allready signed");
				reward.setAnswer(Boolean.FALSE);

			} else
			{
				saLogger.info("Accept Reward(" + reward.getId() + ")");
				reward.setAnswer(Boolean.TRUE);
				getBeliefbase().getBelief("signed").setFact(Boolean.TRUE);
				getBeliefbase().getBeliefSet("contracts").addFact(reward.getServiceOffer());
			}
			IInternalEvent reply = createInternalEvent("rewardReply");
			reply.getParameter("reward").setValue(reward);
			reply.getParameter("task").setValue("replyReward");
			dispatchInternalEvent(reply);
			endAtomic();
		}
	}
}
