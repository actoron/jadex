package deco4mas.examples.V2.tspaces;

import jadex.bdi.runtime.Plan;


/**
 * This Plan is used to update the environment. Right now it is used as an
 * observer to show observed values of agents / the application.
 * 
 * 
 */
@SuppressWarnings("serial")
public class TestPlan2 extends Plan {

	private int counter;

	public void body() {

		System.out.println("Killing in 3 sec...");
		waitFor(3000);		
		
		killAgent();
		System.out.println("Killed myself ...");
	}
}
