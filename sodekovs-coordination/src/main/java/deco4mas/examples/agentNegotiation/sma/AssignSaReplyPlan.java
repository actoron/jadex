package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.deco.AssignReply;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Reply from medium for assigned Sa with Deco4Mas
 */
public class AssignSaReplyPlan extends Plan
{
	public void body()
	{
		Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

		IInternalEvent replyMessage = (IInternalEvent) getReason();
		AssignReply reply = (AssignReply) replyMessage.getParameter("reply").getValue();

		// test if my request
		if ((reply.getInitiator()).equals(this.getComponentIdentifier()))
		{
			System.out.println(this.getComponentName() + " signed with " + reply.getChosenOne().getLocalName());
			smaLogger.info("signed with " + reply.getChosenOne().getLocalName());
			startAtomic();
			// set signed Sa
			getBeliefbase().getBelief("currentSa").setFact(reply.getChosenOne());
			getBeliefbase().getBelief("searchingSa").setFact(Boolean.FALSE);
			endAtomic();
			ValueLogger.addValue("sign_" + reply.getChosenOne().getLocalName(), 1.0);

			// //ping
			// IGoal ping = createGoal("ping");
			// ping.getParameter("receiver").setValue(reply.getChosenOne());
			// dispatchSubgoal(ping);

			// inform Provider
			dispatchSubgoalAndWait(createGoal("informProvideraboutSign"));
		}
	}
}
