package jadex.micro.testcases.servicescope;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;

/**
 * 
 */
@Agent
@Service
public class ProviderAgent implements IExampleService
{
	/**
	 * 
	 */
	public IFuture<String> getInfo()
	{
		return new Future<String>("info");
	}
}
