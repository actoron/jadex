package jadex.bdiv3.examples.shop;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.List;

/**
 * 
 */
@Agent
@Arguments(
{
	@Argument(name="catalog", clazz=List.class), 
	@Argument(name="shopname", clazz=String.class)
})
public class ShopAndCustomerBDI
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected BDIAgent	agent;
	
	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected CustomerCapability	customercap	= new CustomerCapability();

	/** The shop capability. */
	@Capability(beliefmapping=@Mapping(value="money", target="money"))
	protected ShopCapa shopcap	= new ShopCapa((String)agent.getArgument("shopname"), (List<ItemInfo>)agent.getArgument("catalog"));
	
	/** The money. */
	@Belief
	protected double money	= 100.0;
}
