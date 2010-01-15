package jadex.bdi.simulation.client;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.model.Observer;

public class StartPeriodicalObserverPlan extends Plan{

	@Override
	public void body() {
		IInternalEvent event = (IInternalEvent) getReason();
		Observer obs = (Observer) event.getParameter("observer").getValue();
		System.out.println("#InitPeriodicalObserversPlan# TODODODODODODODODODO: "  + obs.getData().getName());
		
	}

}
