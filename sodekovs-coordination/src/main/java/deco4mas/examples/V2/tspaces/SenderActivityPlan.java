package deco4mas.examples.V2.tspaces;

import jadex.bdi.runtime.IInternalEvent;
import jadex.bdi.runtime.Plan;


/**
 * This Plan is used to update the environment. Right now it is used as an
 * observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class SenderActivityPlan extends Plan {

	private int counter;

	public void body() {

		waitFor(3500);		
		

		
		
		counter = 0;
		while (counter < 5) {
			System.out.println("***** Executing CounterPlan: " + counter);
//			getBeliefbase().getBelief("testBelief").setFact(counter);
			getBeliefbase().getBeliefSet("testBelief").addFact(counter);
			
			
			IInternalEvent ie = this.getEventbase().createInternalEvent("testEvent");
			this.getEventbase().dispatchInternalEvent(ie);
						
			waitFor(2500);
			counter++;	
		}
	}
}
