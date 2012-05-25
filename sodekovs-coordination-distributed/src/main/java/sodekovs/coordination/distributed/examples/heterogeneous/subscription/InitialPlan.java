/**
 * 
 */
package sodekovs.coordination.distributed.examples.heterogeneous.subscription;

import java.util.HashMap;

import deco4mas.distributed.coordinate.environment.CoordinationSpace;
import jadex.bdi.runtime.IGoal; 
import jadex.bdi.runtime.Plan;
import jadex.commons.future.ThreadSuspendable;

/**
 * This inital plan waits for 10s and then dispatchtes the 'sayhello' goal, which is observed by the coordination framework.
 * 
 * @author Thomas Preisler
 */
public class InitialPlan extends Plan {

	private static final long serialVersionUID = -3179820895967084220L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.bdi.runtime.Plan#body()
	 */
	@Override
	public void body() {
		// System.out.println("ExampleBDIAgent body() in InitalPlan called.");
		//Get the CoordinationContextID
		CoordinationSpace coordSpace = (CoordinationSpace) getBeliefbase().getBelief("env").getFact();		
		HashMap<String, Object> appArgs = (HashMap<String, Object>) coordSpace.getExternalAccess().getArguments().get(new ThreadSuspendable());
		String coordinationContextID = (String) appArgs.get("CoordinationContextID");
		
		String message = "#I belong to CoordinationContext: "+ coordinationContextID+ "# Hello I'm the example BDI agent and it is a pleasure talking with you!";

		waitFor(2000);

		IGoal goal = createGoal("sayhello");
		goal.getParameter("message").setValue(message);

		// System.out.println("ExampleBDIAgent body() goal 'sayhello' is going to be dispatched with message:");
		System.out.println("BDI sending message: " + message);
		dispatchTopLevelGoal(goal);
	}
}