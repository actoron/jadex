package jadex.micro;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/** 
 *  Micro kernel.
 */
@Imports("jadex.commons.*")
@ProvidedServices({@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(
	expression="new MicroAgentFactory($component.getServiceProvider(), SUtil.createHashMap(new String[]{\"debugger.panels\"},new Object[]{\"jadex.tools.debugger.micro.MicroDebuggerPanel\"}))"))
})
//@Properties(@NameValue(name="system", value="true"))
public class KernelMicroAgent extends MicroAgent
{
}
