package jadex.platform.service.registryv2;

import jadex.bridge.service.ServiceScope;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Dummy agent for test service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=ITestService.class, scope=ServiceScope.NETWORK))
public class NetworkProviderAgent	implements ITestService
{

}
