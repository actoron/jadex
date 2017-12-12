package jadex.micro.testcases.tuplefuture;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.Tuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  Agent implementing the test service.
 */
@Agent
@Service
@ProvidedServices({@ProvidedService(type=ITestService.class)})
public class ProviderAgent implements ITestService
{
	/**
	 *  Example implementation of the method.
	 */
	public ITuple2Future<String, Integer> getSomeResults()
	{
//		Tuple2Future<String, Integer> ret = new Tuple2Future<String, Integer>("hello", Integer.valueOf(99));
		Tuple2Future<String, Integer> ret = new Tuple2Future<String, Integer>();
		ret.setSecondResult(Integer.valueOf(99));
		ret.setFirstResult("hello");
		System.out.println("called method getSomeResults()");
		return ret;
	}
	
//	/**
//	 *  Example implementation of the method.
//	 */
//	public ITuple2Future<String, Integer> getSomeResults2()
//	{
//		return getSomeResults();
//	}
	
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
