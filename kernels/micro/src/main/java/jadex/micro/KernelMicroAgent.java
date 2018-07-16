package jadex.micro;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/** 
 *  Micro kernel.
 */
@Imports("jadex.commons.*")
@ProvidedServices({@ProvidedService(type=IComponentFactory.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(
	expression="new MicroAgentFactory($component, SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.micro.MicroDebuggerPanel\"}))"))
})
@Agent(autostart=@Autostart(value=Boolean3.FALSE, name="kernel_micro"))
@Properties(@NameValue(name="system", value="true"))
public class KernelMicroAgent
{
}