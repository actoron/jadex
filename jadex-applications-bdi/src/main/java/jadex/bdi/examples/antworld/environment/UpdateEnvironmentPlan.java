package jadex.bdi.examples.antworld.environment;


import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * This Plan is used to update the environment.
 * 
 * 
 */
public class UpdateEnvironmentPlan extends Plan {


	public UpdateEnvironmentPlan() {
		System.out.println("11Created: " + this);		
	}


	public void body() {
		
		//Create new ant
		createNewAnt();
		
		//waitRandomly
		long sleepingTime;
		try {
			sleepingTime = SecureRandom.getInstance("SHA1PRNG").nextInt(15) * 1000;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			sleepingTime = 10000;
			e.printStackTrace();
		}
		waitFor(sleepingTime);		
	}

	/**
	 * Creates an ant at a random place 
	 * 	 */
	private void createNewAnt() {
		String agentName = "/jadex/bdi/examples/antworld/Ant.agent.xml";
		IGoal ca = createGoal("ams_create_agent");
		ca.getParameter("type").setValue(agentName);
		
		Map arguments = new HashMap(); // Hack:
		// this works only for agents arguments.put("conf", ap); // that are
		// started on the same platform
		// arguments.put("current_server", appsrv); // ...
//		arguments.put("StartPoint", tsObj.getStartpoint());
		ca.getParameter("arguments").setValue(arguments);
		dispatchSubgoalAndWait(ca);		
	}
}