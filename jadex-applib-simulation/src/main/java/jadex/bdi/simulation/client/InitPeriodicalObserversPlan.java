package jadex.bdi.simulation.client;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.simulation.helper.Constants;
import jadex.bdi.simulation.model.Observer;

import java.util.ArrayList;

public class InitPeriodicalObserversPlan extends Plan {

	@Override
	public void body() {
		// TODO Auto-generated method stub
		ArrayList observers = (ArrayList) getParameter(Constants.PERIODICAL_OBSERVER_LIST).getValue();
		
		System.out.println("#InitPeriodicalObserversPlan# Elementname: "  + ((Observer)observers.get(0)).getData().getName());
		
		for(int i=0; i < observers.size(); i++){
			IInternalEvent event = createInternalEvent("StartPeriodicalObserverEvent");
			event.getParameter("observer").setValue((Observer) observers.get(i));
			dispatchInternalEvent(event);
		}			
	}
}

