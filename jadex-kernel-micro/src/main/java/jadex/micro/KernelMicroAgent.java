package jadex.micro;

import jadex.bridge.IComponentFactory;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Micro kernel.
 */
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.micro.MicroAgentFactory($component.getServiceProvider(), null)"))
})
public class KernelMicroAgent extends MicroAgent
{
}
