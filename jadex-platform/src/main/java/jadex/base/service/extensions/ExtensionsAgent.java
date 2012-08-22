package jadex.base.service.extensions;

import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.IExtensionLoaderService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

@Imports(
{
	"jadex.bridge.service.types.factory.*",
	"jadex.bridge.service.types.cms.*"
})
@Arguments(@Argument(name="extensions", clazz=String.class))
@ProvidedServices(@ProvidedService(type=IExtensionLoaderService.class, implementation=@Implementation(ExtensionLoaderService.class)))
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM)))
@Agent
public class ExtensionsAgent
{
}
