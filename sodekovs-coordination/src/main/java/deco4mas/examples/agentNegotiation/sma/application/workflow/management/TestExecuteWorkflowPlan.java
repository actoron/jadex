package deco4mas.examples.agentNegotiation.sma.application.workflow.management;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import deco4mas.examples.agentNegotiation.common.dataObjects.RequiredService;
import deco4mas.examples.agentNegotiation.common.dataObjects.ServiceContract;
import deco4mas.examples.agentNegotiation.common.dataObjects.WorkflowData;

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
			// startAtomic();
			Boolean execute = true;
			ServiceContract contract = (ServiceContract) satisfiedService.getParameter("contract").getValue();
			RequiredService[] services = (RequiredService[]) getBeliefbase().getBeliefSet("requiredServices").getFacts();
			for (RequiredService service : services)
			{
				synchronized (service.getMonitor())
				{
					if (contract.getServiceType().getName().equals(service.getServiceType().getName()))
					{
						service.setContract(contract);
						service.setSearching(false);
						((WorkflowData)getBeliefbase().getBelief("workflowData").getFact()).addNegotiation();
						dispatchInternalEvent(createInternalEvent("returnExecution"));
					}
					if (service.isSearching())
					{
						execute = false;
					}
				}
			}
			// endAtomic();
			if (execute && !(Boolean) getBeliefbase().getBelief("executionPhase").getFact())
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
