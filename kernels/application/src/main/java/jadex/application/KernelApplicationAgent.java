package jadex.application;

import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.Boolean3;
import jadex.component.ComponentComponentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Component kernel.
 */
@Properties({
	@NameValue(name="system", value="true"),
	@NameValue(name="kernel.types", value="new String[] {\"application.xml\"}"),
	@NameValue(name="kernel.componenttypes", value="new String[]{\""+ApplicationComponentFactory.FILETYPE_APPLICATION+"\"}")
})
@ProvidedServices({
	@ProvidedService(type=IComponentFactory.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.application.ApplicationComponentFactory($component)"))
})
@Agent(name="kernel_application",
	autostart=Boolean3.FALSE)
public class KernelApplicationAgent
{
}
