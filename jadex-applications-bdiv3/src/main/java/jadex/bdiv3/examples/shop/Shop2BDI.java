package jadex.bdiv3.examples.shop;

import jadex.bdiv3.BDIAgent;
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
	@Agent
	protected BDIAgent	agent;
	
	// Principles: 
	// - each belief should only be represented as one field! (no assignments)
	// - access of beliefs of capabilities via getters/setters
	// - delegation to the outside via own getter/setters (allows renaming)
	// - abstract beliefs need to be declared via native getter/setter pairs
	
//	@Capability
//	@AgentArgument("shopname")//, target="shopname")
//	@AgentArgument("catalog")//, target="catalog")
	protected ShopCapa shopcap	= new ShopCapa((String)agent.getArgument("shopname"), (List<ItemInfo>)agent.getArgument("catalog"));
	
	
	
	
	
	
	// Money is abstract in capa
//	/** The money. */
//	@Belief//(assign="shopcap.money")
//	protected double money = shopcap.money;

//	/** The shop name. */
//	@AgentArgument
//	@Belief//(ref="shopcap.shopname")
//	protected String shopname = shopcap.shopname;
	
//	/** The shop catalog. */
//	@AgentArgument
//	@Belief
//	protected List<ItemInfo> catalog = shopcap.catalog;
}
