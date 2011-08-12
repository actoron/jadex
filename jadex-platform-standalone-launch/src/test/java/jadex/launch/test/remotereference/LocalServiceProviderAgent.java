package jadex.launch.test.remotereference;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent providing the local service.
 */
@Agent
@Imports("jadex.micro.*")
@ProvidedServices(@ProvidedService(type=ILocalService.class, implementation=@Implementation(expression="((IPojoMicroAgent)$component).getPojoAgent()")))
public class LocalServiceProviderAgent implements ILocalService
{

}
