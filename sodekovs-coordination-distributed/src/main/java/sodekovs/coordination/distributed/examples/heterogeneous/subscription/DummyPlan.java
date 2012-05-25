package sodekovs.coordination.distributed.examples.heterogeneous.subscription;

import jadex.bdi.runtime.Plan;

/**
 * Just a dummy plan which is consumes the 'sayhello' goal event so that no warning occurs.
 * 
 * @author Thomas Preisler
 */
public class DummyPlan extends Plan {

	private static final long serialVersionUID = 3263000467677892051L;

	@Override
	public void body() {
		// do nothing
	}
}