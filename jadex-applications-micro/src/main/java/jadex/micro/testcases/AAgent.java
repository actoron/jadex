package jadex.micro.testcases;

import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@ProvidedServices(@ProvidedService(type=IAService.class, implementation=@Implementation(expression="$component")))
public class AAgent extends MicroAgent implements IAService
{
}
