package jadex.platform.service.registryv2;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;

/**
 *  Dummy agent for local test service.
 *  Should never be found due to platform publication scope.
 */
@Agent(autoprovide=Boolean3.TRUE)
public class LocalProviderAgent	implements ITestService
{
}
