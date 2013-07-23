package jadex.micro.testcases.serviceimpl;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IInfoService.class))
public class MicroProviderAgent extends MicroAgent implements IInfoService
{
	/**
	 *  Get some info.
	 *  @return Some info.
	 */
	public IFuture<String> getInfo()
	{
		return new Future<String>("some info");
	}
}
