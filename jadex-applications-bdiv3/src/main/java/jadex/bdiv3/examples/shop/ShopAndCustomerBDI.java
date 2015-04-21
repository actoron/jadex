package jadex.bdiv3.examples.shop;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Mapping;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;


/**
 * 
 */
@Agent
@Arguments(
{
	@Argument(name="catalog", clazz=List.class, defaultvalue="ShopBDI.getDefaultCatalog()"), 
	@Argument(name="shopname", clazz=String.class, defaultvalue="jadex.commons.SUtil.createUniqueId(\"Shop\",2)")
})
public class ShopAndCustomerBDI
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")
	protected IInternalAccess agent;
	
	/** The customer capability. */
	@Capability(beliefmapping=@Mapping("money"))
	protected CustomerCapability customercap = new CustomerCapability();

	/** The shop capability. */
	@Capability(beliefmapping=@Mapping(value="money", target="money"))
	protected ShopCapa shopcap = new ShopCapa((String)agent.getComponentFeature(IArgumentsFeature.class).getArguments().get("shopname"), 
		(List<ItemInfo>)agent.getComponentFeature(IArgumentsFeature.class).getArguments().get("catalog"));
	
	/** The money. */
	@Belief
	protected double money	= 100.0;
}
