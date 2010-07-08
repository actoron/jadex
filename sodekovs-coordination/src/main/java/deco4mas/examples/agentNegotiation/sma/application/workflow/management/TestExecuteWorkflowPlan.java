package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import deco4mas.examples.agentNegotiation.sma.application.RequiredService;

/**
 * Test if all neededServices found a SA
 */
public class TestExecuteWorkflowPlan extends Plan
{

	public void body()
	{
		try
		{
			IInternalEvent satisfiedService = (IInternalEvent) getReason();
			startAtomic();
			Boolean execute = true;
			RequiredService[] services = (RequiredService[]) getBeliefbase().getBeliefSet("requiredServices").getFacts();
			for (RequiredService service : services)
			{
				synchronized (service.getMonitor())
				{
					Integer int1 = (Integer) satisfiedService.getParameter("id").getValue();
					Integer int2 = service.getId();
					if (int1.equals(int2))
					{
						service.setSa((IComponentIdentifier)satisfiedService.getParameter("sa").getValue());
						service.setSearching(false);
					}
					if (service.isSearching())
					{
						execute = false;
					}
				}
			}
			endAtomic();
			if (execute)
			{
				dispatchTopLevelGoal(createGoal("startWorkflow"));
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
