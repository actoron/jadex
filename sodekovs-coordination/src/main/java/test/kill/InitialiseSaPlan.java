package test.kill;

import jadex.bdi.runtime.Plan;

/**
 * Init SAs
 */
public class InitialiseSaPlan extends Plan {


	public void body() {
		System.out.println("New Master Agent...");
//		waitFor(1);
		
			killAgent();
	}

}
