package jadex.micro.testcases.seqfuture;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ISequenceFuture;
import jadex.commons.future.SequenceFuture;
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
		System.out.println("called method getSomeResults()");
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public IIntermediateFuture<String> getSomeResults()
//	{
//		IntermediateFuture<String> ret = new IntermediateFuture<String>(Arrays.asList(new String[]{"a", "b"}));
//		System.out.println("called method getSomeResults()");
//		return ret;
//	}
}
