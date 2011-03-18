/**
 * 
 */
package deco4mas.examples.heterogeneous;

import jadex.bdi.runtime.Plan;

/**
 * This plan is triggered by the 'receivehello' internal event and just prints out the parameter received over the coordination framework.
 * 
 * @author Thomas Preisler
 */
public class ReceiveHelloPlan extends Plan {

	private static final long serialVersionUID = -2676568690333782525L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see jadex.bdi.runtime.Plan#body()
	 */
	@Override
	public void body() {
		String message = (String) getParameter("message").getValue();
		System.out.println("ExampleBDIAgent body() in ReceiveHelloPlan was triggered by the internalevent 'receivehello' with message:");
		System.out.println("\t" + message);
	}
}