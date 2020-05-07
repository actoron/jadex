package jadex.bdiv3.testcases.capabilities;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ICapability;
import jadex.commons.future.Future;

@Capability
public class TestCapabilityBDI
{
	// done when plan was run
	Future<Void>	result	= new Future<Void>();
		
	@Capability
	TestSubCapabilityBDI	subcapa	= new TestSubCapabilityBDI();

	@Belief
	boolean runplan1	= true;

	@Plan(trigger=@Trigger(factchangeds = "runplan1"))
	void plan1(ICapability capa) {
		result.setResult(null);
	}
}
