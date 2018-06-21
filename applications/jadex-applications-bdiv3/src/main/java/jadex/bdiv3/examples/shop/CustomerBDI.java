package jadex.bdiv3.examples.shop;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.micro.annotation.Agent;

/**
 *  Customer capability.
 */
@Agent
public class CustomerBDI
{
	//-------- attributes --------

	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected CustomerCapability	cap	= new CustomerCapability();
	
	/** The money. */
	@Belief
	protected double	money	= 100;
}
