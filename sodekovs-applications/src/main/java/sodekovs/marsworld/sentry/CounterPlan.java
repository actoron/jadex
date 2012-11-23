package sodekovs.marsworld.sentry;

import jadex.bdi.runtime.Plan;

@SuppressWarnings("serial")
public class CounterPlan extends Plan{

	@Override
	public void body() {
		
		while(true){
		int counter = (Integer) getBeliefbase().getBelief("tmpCounter").getFact();
		counter++;
		getBeliefbase().getBelief("tmpCounter").setFact(counter);
		
		waitFor(1000);
		}
	}
}