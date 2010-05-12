package deco4mas.examples.agentNegotiation.sma;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import deco4mas.examples.agentNegotiation.deco.AssignRequest;
import deco4mas.examples.agentNegotiation.deco.Evaluator;
import deco4mas.examples.agentNegotiation.deco.ServiceAgentData;
import deco4mas.examples.agentNegotiation.deco.negMedium.NegSpaceMechanism;

/**
 * Assign a Sa with Deco4mas
 */
public class AssignSaDecoPlan extends Plan
{
	public void body()
	{
		if (getBeliefbase().getBelief("currentSa").getFact() == null)
		{
			System.out.println(getComponentName() + ": Assign a Sa with deco");

			// Internal Event for assign Sa
			IInternalEvent assignSaEvent = createInternalEvent("assignSa");
			// evaluator
			ServiceAgentData serviceData = new ServiceAgentData();
			Evaluator evaluator = new Evaluator(serviceData);
			// request
			AssignRequest request = new AssignRequest(this.getComponentIdentifier(), (String) getBeliefbase().getBelief("allocatedService")
				.getFact(), evaluator, NegSpaceMechanism.NAME);

			assignSaEvent.getParameter("request").setValue(request);
			assignSaEvent.getParameter("task").setValue("assignSaRequest");
			dispatchInternalEvent(assignSaEvent);
		} else
		{
			dispatchSubgoalAndWait(createGoal("informProvideraboutSign"));
		}

	}
}
