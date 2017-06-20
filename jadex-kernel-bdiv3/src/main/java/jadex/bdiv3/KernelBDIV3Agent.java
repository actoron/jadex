package jadex.bdiv3;


import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
//@Properties({@NameValue(name="system", value="true"), @NameValue(name="kernel.types", value="new String[]{\"BDI.class\"}")})
@Properties({@NameValue(name="system", value="true"), @NameValue(name="kernel.types", value="new String[]{\"BDI.class\"}")})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(
//	expression="new jadex.bdiv3.BDIAgentFactory($component)"))
	expression="new jadex.bdiv3.BDIAgentFactory($component, jadex.commons.SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.bdiv3.BDIViewerDebuggerPanel\"}))"))
})
@Agent
public class KernelBDIV3Agent 
{
}
