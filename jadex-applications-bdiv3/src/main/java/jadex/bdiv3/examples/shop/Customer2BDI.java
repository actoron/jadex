package jadex.bdiv3.examples.shop;

import jadex.bdiv3.annotation.Belief;
import jadex.micro.annotation.Agent;

/**
 *  Customer capability.
 */
@Agent
public class Customer2BDI
{
	//-------- attributes --------

	/** The capability. */
//	@Capability(assignto={@Mapping("money", target="money")})
	protected CustomerCapability	cap	= new CustomerCapability();
	
	/** The money. */
	@Belief
	protected double	money	= 100;
}
