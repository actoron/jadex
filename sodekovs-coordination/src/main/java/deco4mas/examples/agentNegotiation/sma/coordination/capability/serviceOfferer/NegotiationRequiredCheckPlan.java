package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;

/**
 * Test if negotiation is required
 */
public class NegotiationRequiredCheckPlan extends Plan
{
	public void body()
	{
		try
		{
			// get Logger
			Logger smaLogger = AgentLogger.getTimeEvent(this.getComponentName());

			smaLogger.info("Start assign all needed services");
			// assign all needed services
			IInternalEvent event = (IInternalEvent) getReason();
			RequiredService service = (RequiredService) event.getParameter("service").getValue();

			// sign end
			if (service.isRemoved())
			{
				synchronized (service.getMonitor())
				{
					smaLogger.info("Send SignEnd to " + service.getContract().getParticipant().getLocalName());
					IMessageEvent me = createMessageEvent("informMessage");
					List cis = new LinkedList();
					cis.add(service.getContract().getParticipant());
					me.getParameter("receivers").setValue(cis);
					me.getParameter("content").setValue("sign end");
					sendMessage(me);
				}
			} else if (service.isSearching())
			{
				if (service.getContract() != null)
				{
					synchronized (service.getMonitor())
					{
						smaLogger.info("Send SignEnd to " + service.getContract().getParticipant().getLocalName());
						IMessageEvent me = createMessageEvent("informMessage");
						List cis = new LinkedList();
						cis.add(service.getContract().getParticipant());
						me.getParameter("receivers").setValue(cis);
						me.getParameter("content").setValue("sign end");
						sendMessage(me);
					}
				}
				IGoal assign = createGoal("initiateNegotiation");
				assign.getParameter("requiredService").setValue(service);
				dispatchTopLevelGoal(assign);
			}
			// }
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
