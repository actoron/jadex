package jadex.extension.envsupport;

import jadex.bridge.service.types.factory.IComponentFactoryExtensionService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Imports(
{
	"jadex.bridge.service.types.factory.*"
})
@ProvidedServices(@ProvidedService(name="envextension", type=IComponentFactoryExtensionService.class, implementation=@Implementation(EnvSupportExtensionService.class)))
@Agent
public class EnvSupportAgent
{
}
