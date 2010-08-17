package deco4mas.examples.agentNegotiation.sma.coordination.capability.serviceOfferer;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
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

			if (service.isSearching())
			{
				IGoal assign = createGoal("initiateNegotiation");
				assign.getParameter("requiredService").setValue(service);
				dispatchTopLevelGoal(assign);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
