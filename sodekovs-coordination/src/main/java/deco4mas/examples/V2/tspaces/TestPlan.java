package deco4mas.examples.V2.tspaces;

import jadex.bdi.runtime.Plan;


/**
 * This Plan is used to update the environment. Right now it is used as an
 * observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class TestPlan extends Plan {

	private int counter;

	public void body() {

		waitFor(1000);		
		
		counter = 0;
		StringBuffer tmp = new StringBuffer();
		while (counter < 3) {
			tmp.append(counter);
			System.out.println("***** TestPlan at "  + this.getComponentName() + " : "  + tmp.toString());
			waitFor(2500);
			counter++;
			
		}
	}
}
