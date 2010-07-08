package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import deco4mas.examples.agentNegotiation.evaluate.AgentLogger;
import deco4mas.examples.agentNegotiation.sma.application.RequiredService;

/**
 * Test if negotiation or information about sign end is required
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
			// RequiredService[] services = (RequiredService[])
			// getBeliefbase().getBeliefSet("requiredServices").getFacts();
			// for (RequiredService service : services)
			// {
			if (service.isRemoved())
			{
				synchronized (service.getMonitor())
				{
					smaLogger.info("Send SignEnd to " + service.getSa().getLocalName());
					IMessageEvent me = createMessageEvent("informMessage");
					List cis = new LinkedList();
					cis.add(service.getSa());
					me.getParameter("receivers").setValue(cis);
					me.getParameter("content").setValue("sign end");
					sendMessage(me);
				}
			} else if (service.isSearching())
			{
				if (service.getSa() != null)
				{
					synchronized (service.getMonitor())
					{
						smaLogger.info("Send SignEnd to " + service.getSa().getLocalName());
						IMessageEvent me = createMessageEvent("informMessage");
						List cis = new LinkedList();
						cis.add(service.getSa());
						me.getParameter("receivers").setValue(cis);
						me.getParameter("content").setValue("sign end");
						sendMessage(me);
					}
				}
				IGoal assign = createGoal("searchSa");
				assign.getParameter("service").setValue(service);
				dispatchSubgoalAndWait(assign);
			}
			// }
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
