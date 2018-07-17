package jadex.bdiv3.examples.shop;

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
 * 
 */
@Agent(type=BDIAgentFactory.TYPE)
@Arguments(
{
	@Argument(name="catalog", clazz=List.class, defaultvalue="ShopBDI.getDefaultCatalog()"), 
	@Argument(name="shopname", clazz=String.class, defaultvalue="jadex.commons.SUtil.createPlainRandomId(\"Shop\",2)")
})
public class ShopAndCustomerAgent
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
	protected ShopCapa shopcap = new ShopCapa((String)agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("shopname"), 
		(List<ItemInfo>)agent.getFeature(IArgumentsResultsFeature.class).getArguments().get("catalog"));
	
	/** The money. */
	@Belief
	protected double money	= 100.0;
}
