package jadex.platform.service.parallelizer;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ISequentialService.class, 
	implementation=@Implementation(expression="$pojoagent")))
public class SeqAgent implements ISequentialService
{
	/**
	 * 
	 */
	public IFuture<String> doSequential(String data)
	{
		return new Future<String>("result of: "+data);
	}
}
