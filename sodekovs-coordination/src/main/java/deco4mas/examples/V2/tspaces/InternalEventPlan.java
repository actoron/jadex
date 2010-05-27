package deco4mas.examples.V2.tspaces;

import jadex.bdi.runtime.Plan;

/**
 * Plan to handle the internalEvent "testEvent"
 * 
 */
@SuppressWarnings("serial")
public class InternalEventPlan extends Plan {

	public void body() {

		System.out.println("***** InternalEvent testEvent dispatched at " + this.getComponentName());

	}
}
