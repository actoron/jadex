package jadex.micro;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/** 
 *  Micro kernel.
 */
@Imports("jadex.commons.*")
@ProvidedServices({@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(
	expression="new MicroAgentFactory($component.getServiceProvider(), SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.micro.MicroDebuggerPanel\"}))"))
})
public class KernelMicroAgent extends MicroAgent
{
}
