package jadex.platform.service.registryv2;

import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;

/**
 *  Dummy agent for test service.
 */
@Agent(autoprovide=Boolean3.TRUE)
public class ProviderAgent	implements ITestService
{

}
