package jadex.bdiv3;


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
 *  Component kernel.
 */
//@Properties({@NameValue(name="system", value="true"), @NameValue(name="kernel.types", value="new String[]{\"BDI.class\"}")})
// multi factory only uses .class (and BDI.class as marker to know which kernels have been started)
@Properties({@NameValue(name="system", value="true"), @NameValue(name="kernel.types", value="new String[]{\".class\"}")})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(
//	expression="new jadex.bdiv3.BDIAgentFactory($component)"))
	expression="new jadex.bdiv3.BDIAgentFactory($component, jadex.commons.SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.bdiv3.BDIViewerDebuggerPanel\"}))"))
})
@Agent(name="kernel_bdi",
	autostart=Boolean3.FALSE,
	predecessors="jadex.platform.service.security.SecurityAgent")
public class KernelBDIV3Agent 
{
}
