package jadex.bdiv3.examples.shop;

import java.util.ArrayList;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 *  Shop bdi agent.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Arguments(
{
	@Argument(name="catalog", clazz=List.class, defaultvalue="ShopAgent.getDefaultCatalog()"), 
	@Argument(name="shopname", clazz=String.class, defaultvalue="jadex.commons.SUtil.createPlainRandomId(\"Shop\",2)")
})
public class ShopAgent
{
	//-------- attributes --------

	@Agent
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	protected IInternalAccess agent;
	
	// Principles: 
	// - each belief should only be represented as one field! (no assignments)
	// - access of beliefs of capabilities via getters/setters
	// - delegation to the outside via own getter/setters (allows renaming)
	// - abstract beliefs need to be declared via native getter/setter pairs
	
	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected ShopCapa shopcap	= new ShopCapa((String)agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("shopname"), 
		(List<ItemInfo>)agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("catalog"));
	
	/** The money. */
	@Belief
	protected double	money	= 100;
	
	/**
	 *  Get some default catalog.
	 */
	public static List<ItemInfo> getDefaultCatalog()
	{
		List<ItemInfo> ret = new ArrayList<ItemInfo>();
		ret.add(new ItemInfo("Paper", 0.89, 10));
		ret.add(new ItemInfo("Pencil", 0.56, 2));
		return ret;
	}
}
