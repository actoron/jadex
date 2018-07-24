package jadex.component;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
@Properties({
	@NameValue(name="system", value="true"),
	@NameValue(name="kernel.types", value="new String[] {\"component.xml\"}")
})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, implementation=@Implementation(expression="new jadex.component.ComponentComponentFactory($component)"))
})
@Agent(autostart=@Autostart(value=Boolean3.FALSE, name="kernel_component"))
public class KernelComponentAgent
{
}
