/**
 * 
 */
package deco4mas.examples.iograph;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;

/**
 * This plan is triggered by the 'receive_message' internal event and just prints out the parameter received over the coordination framework.
 * 
 * @author Thomas Preisler
 */
public class ReceiveMessagePlan extends Plan {

	private static final long serialVersionUID = -2676568690333782525L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.bdi.runtime.Plan#body()
	 */
	@Override
	public void body() {
		String id = (String) getBeliefbase().getBelief("graphId").getFact();
		String message = (String) getParameter("message").getValue();
		System.out.println("ReceiveMessagePlan called in BDIGraphAgent " + id + " receiving the id from GraphAgent " + message);
		
		IGoal goal = createGoal("send_message");
		goal.getParameter("message").setValue(id);
		dispatchTopLevelGoal(goal);
	}
}