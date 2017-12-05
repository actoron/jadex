package jadex.micro.testcases.search;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type = ITestService.class))
public class ProviderAgent implements ITestService 
{
}
