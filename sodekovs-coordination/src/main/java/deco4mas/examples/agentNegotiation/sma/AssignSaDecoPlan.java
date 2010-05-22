package deco4mas.examples.agentNegotiation.sma;

import java.util.HashMap;
import java.util.Map;
import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import deco4mas.examples.agentNegotiation.ServiceType;
import deco4mas.examples.agentNegotiation.deco.AssignRequest;
import deco4mas.examples.agentNegotiation.deco.RequestInformation;
import deco4mas.examples.agentNegotiation.deco.negMedium.NegSpaceMechanism;
import deco4mas.examples.agentNegotiation.sma.strategy.SimpleSelectionStrategy;
import deco4mas.examples.agentNegotiation.sma.strategy.HistorytimeTrustFunction;
import deco4mas.examples.agentNegotiation.sma.strategy.WeightFactorUtilityFunction;

/**
 * Assign a Sa with Deco4mas
 */
public class AssignSaDecoPlan extends Plan
{
	public void body()
	{
//		if (getBeliefbase().getBelief("currentSa").getFact() == null)
//		{
		getBeliefbase().getBelief("currentSa").setFact(null);
			System.out.println(getComponentName() + ": Assign a Sa with deco");

			// Internal Event for assign Sa
			IInternalEvent assignSaEvent = createInternalEvent("assignSa");
			
			ServiceType myService = (ServiceType) getBeliefbase().getBelief("allocatedService").getFact();
			
			ServiceAgentHistory history = (ServiceAgentHistory) getBeliefbase().getBelief("history").getFact();
			//trustFunc
			HistorytimeTrustFunction trustFunction = new HistorytimeTrustFunction(history);
			// utilityFunc
			WeightFactorUtilityFunction utilityFunction = new WeightFactorUtilityFunction(trustFunction);
			utilityFunction.addFactor("cost", 10.0,  myService.getMaxCost(),myService.getMinCost(), false);
			utilityFunction.addFactor("duration", 5.0,  myService.getMaxDuration(),myService.getMinDuration(), false);
			utilityFunction.addFactor("trust", 25.0, 10.0, 0.0, true);
			//Selector
			SimpleSelectionStrategy selector = new SimpleSelectionStrategy();
			//extra info
			Map<String, Object> information = new HashMap<String, Object>();
			information.put("deadline", 3000L);
			RequestInformation info = new RequestInformation(information);
			// request
			AssignRequest request = new AssignRequest(this.getComponentIdentifier(), myService ,utilityFunction, selector, NegSpaceMechanism.NAME, info);

			assignSaEvent.getParameter("request").setValue(request);
			assignSaEvent.getParameter("task").setValue("assignSaRequest");
			dispatchInternalEvent(assignSaEvent);
//		} else
//		{
//			dispatchSubgoalAndWait(createGoal("informProvideraboutSign"));
//		}

	}
}
