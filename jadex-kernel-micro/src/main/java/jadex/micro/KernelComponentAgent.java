package jadex.micro;

import jadex.bridge.IComponentFactory;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.component.ComponentComponentFactory($component.getServiceProvider())"))
})
public class KernelComponentAgent extends MicroAgent
{
}
