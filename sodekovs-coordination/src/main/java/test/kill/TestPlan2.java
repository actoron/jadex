package test.kill;

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

		waitFor(3000);
		System.out.println("Killing one agent...");
				
		
		killAgent();
		System.out.println("Killed myself ...");
	}
}
