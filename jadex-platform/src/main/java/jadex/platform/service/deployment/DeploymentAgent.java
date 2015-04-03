package jadex.platform.service.deployment;

import jadex.bridge.service.types.deployment.IDeploymentService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent that provides the deployment service.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IDeploymentService.class, implementation=@Implementation(DeploymentService.class)))
@Properties(value=@NameValue(name="system", value="true"))
public class DeploymentAgent
{
}
