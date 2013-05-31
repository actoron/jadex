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
public class Shop2BDI
{
	//-------- attributes --------

	@Agent
	protected BDIAgent	agent;
	
	// Principles: 
	// - each belief should only be represented as one field! (no assignments)
	// - access of beliefs of capabilities via getters/setters
	// - delegation to the outside via own getter/setters (allows renaming)
	// - abstract beliefs need to be declared via native getter/setter pairs
	
	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected ShopCapa shopcap	= new ShopCapa((String)agent.getArgument("shopname"), (List<ItemInfo>)agent.getArgument("catalog"));
	
	/** The money. */
	@Belief
	protected double	money	= 100;
}
