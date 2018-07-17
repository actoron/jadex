package jadex.bdiv3.examples.shop;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.micro.annotation.Agent;

/**
 *  Customer capability.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class CustomerAgent
{
	//-------- attributes --------

	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected CustomerCapability	cap	= new CustomerCapability();
	
	/** The money. */
	@Belief
	protected double	money	= 100;
}
