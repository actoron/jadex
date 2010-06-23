package deco4mas.examples.agentNegotiation.sma.behaviour;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.decoMAS.dataObjects.AssignReply;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.evaluate.ValueLogger;
import deco4mas.examples.agentNegotiation.sma.workflow.management.NeededService;

/**
 * Reply from medium for assigned Sa with Deco4Mas
 */
public class AssignSaReplyPlan extends Plan
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

				startAtomic();
				// set signed Sa
				NeededService[] services = (NeededService[]) getBeliefbase().getBeliefSet("neededServices").getFacts();
				for (NeededService service : services)
				{
					if (service.getServiceType().getName().equals(reply.getServiceType().getName()))
					{
						service.setSa(reply.getChosenOne());
						service.setSearching(false);
					}
				}

				endAtomic();
				ValueLogger.addValue("sign_" + reply.getChosenOne().getLocalName(), 1.0);

				// test if the workflow can be executed
				dispatchInternalEvent(createInternalEvent("serviceFound"));
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}

	}
}
