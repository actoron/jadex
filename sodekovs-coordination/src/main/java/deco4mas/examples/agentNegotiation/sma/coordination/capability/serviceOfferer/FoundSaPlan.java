package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.AssignReply;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;

/**
 * Reply from medium for assigned Sa with Deco4Mas
 */
public class FoundSaPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			// get reply
			IInternalEvent replyMessage = (IInternalEvent) getReason();
			AssignReply reply = (AssignReply) replyMessage.getParameter("reply").getValue();

			// test if my request
			if ((reply.getInitiator()).equals(this.getComponentIdentifier()))
			{
				// LOG
				System.out.println(this.getComponentName() + " signed with " + reply.getChosenOne().getLocalName());
				smaLogger.info("signed with " + reply.getChosenOne().getLocalName() + " for " + reply.getServiceType().getName());

				ValueLogger.addValue("sign_" + reply.getChosenOne().getLocalName(), 1.0);

				// test if the workflow can be executed
				IInternalEvent satisfy = createInternalEvent("serviceSatisfied");
				satisfy.getParameter("id").setValue(reply.getId());
				satisfy.getParameter("sa").setValue(reply.getChosenOne());
				dispatchInternalEvent(satisfy);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}

	}
}
