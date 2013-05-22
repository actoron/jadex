/**
 * 
 */
package sodekovs.marsworld.convergence;

import jadex.bdi.runtime.Plan;

/**
 * Simple Plan that waits for one second and then increments the no_msg_received belief by one.
 * 
 * @author Thomas Preisler
 */
public class CountNoMsgPlan extends Plan {

	private static final long serialVersionUID = -5479366479417141738L;

	@Override
	public void body() {
		// wait for 
		waitFor(30000);
		
		while (true) {
			// wait for one second
			waitFor(1000);
			
			Integer oldValue = (Integer) getBeliefbase().getBelief("no_msg_received").getFact();
			Integer newValue = oldValue + 1;
			getBeliefbase().getBelief("no_msg_received").setFact(newValue);
		}
	}
}