package jadex.platform.service.registryv2;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Dummy agent for test service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, scope=Binding.SCOPE_GLOBAL))
public class ProviderAgent	implements ITestService
{

}
