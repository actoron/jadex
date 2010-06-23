package deco4mas.examples.agentNegotiation.sma.workflow.management;

import jadex.bdi.runtime.Plan;

/**
 * Test if all neededServices found a SA
 */
public class TestExecuteWorkflowPlan extends Plan
{

	public void body()
	{
		try
		{
			startAtomic();
			Boolean execute = true;
			NeededService[] services = (NeededService[]) getBeliefbase().getBeliefSet("neededServices").getFacts();
			for (NeededService service : services)
			{
				if (!service.alreadySelected())
				{
					execute = false;
				}
			}
			if (execute)
			{
				dispatchTopLevelGoal(createGoal("startWorkflow"));
			}
			endAtomic();
		} catch (Exception e)
		{
			e.printStackTrace();
			fail(e);
		}
	}
}
