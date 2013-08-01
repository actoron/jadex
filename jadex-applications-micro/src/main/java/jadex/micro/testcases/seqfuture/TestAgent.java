package jadex.micro.testcases.seqfuture;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 * 
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=ITestService.class))
public class TestAgent implements ITestService
{
	/**
	 * 
	 */
	public ISequenceFuture<String, Integer> getSomeResults()
	{
		SequenceFuture<String, Integer> ret = new SequenceFuture<String, Integer>("hello", new Integer(1));
		return ret;
	}
}
