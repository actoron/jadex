package jadex.micro.testcases.serviceinheritance;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IExtendedService.class, implementation=@Implementation(expression="$pojoagent")))
public class ProviderAgent implements IExtendedService
{
	/**
	 *  Example method returning some string value.
	 *  @return Some basic info.
	 */
	public IFuture<String> getBasicInfo()
	{
//		System.out.println("hello: "+ServiceCall.getCurrentInvocation().getTimeout());
		
		return new Future<String>("basic info");
	}
	
	/**
	 *  Example method returning some string value.
	 *  @return Some extended info.
	 */
	public IFuture<String> getExtendedInfo()
	{
		return new Future<String>("extended info");
	}

}
