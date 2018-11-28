package jadex.bdiv3x;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel for XML BDI agents.
 */
@Agent(name="kernel_bdix",
	autostart=Boolean3.FALSE,
	predecessors="jadex.platform.service.security.SecurityAgent")
@Properties({
	@NameValue(name="kernel.types", value="new String[]{\"agent.xml\", \"capability.xml\"}")
})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.bdiv3x.BDIXComponentFactory($component, jadex.commons.SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.bdiv3.BDIViewerDebuggerPanel\"}))"))
})
public class KernelBDIXAgent
{

}
