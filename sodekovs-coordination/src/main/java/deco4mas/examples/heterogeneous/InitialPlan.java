/**
 * 
 */
package deco4mas.examples.heterogeneous;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

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
//		System.out.println("ExampleBDIAgent body() in InitalPlan called.");
		String message = "Hello I'm the example BDI agent and it is a pleasure talking with you!";

		waitFor(2000);

		IGoal goal = createGoal("sayhello");
		goal.getParameter("message").setValue(message);

//		System.out.println("ExampleBDIAgent body() goal 'sayhello' is going to be dispatched with message:");
		System.out.println("BDI sending message" + message);
		dispatchTopLevelGoal(goal);
	}
}