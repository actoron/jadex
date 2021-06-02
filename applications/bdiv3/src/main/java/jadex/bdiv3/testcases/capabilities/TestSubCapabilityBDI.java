package jadex.bdiv3.testcases.capabilities;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.commons.future.Future;

@Capability
public class TestSubCapabilityBDI
{	
	// done when plan was run
	Future<Void>	result	= new Future<Void>();

	@Belief
	boolean runplan2	= true;

	@Plan(trigger=@Trigger(factchangeds = "runplan2"))
	void plan2(ICapability capa) {
		result.setResult(null);
	}
}
