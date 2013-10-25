package jadex.bdiv3;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
@Properties({
	@NameValue(name="kernel.types", value="new String[] { \"BDI.class\"}")
})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(
//	expression="new jadex.bdiv3.BDIAgentFactory($component.getServiceProvider())"))
	expression="new jadex.bdiv3.BDIAgentFactory($component.getServiceProvider(), jadex.commons.SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.bdiv3.BDIViewerDebuggerPanel,jadex.tools.debugger.bdiv3.BDIAgentInspectorDebuggerPanel\"}))"))
})
public class KernelBDIV3Agent extends MicroAgent
{
}
