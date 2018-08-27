package jadex.micro.testcases.lazyinject;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateFuture;
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
	public ITuple2Future<String, Integer> getFirstTupleResult()
	{
		Tuple2Future<String, Integer> ret = new Tuple2Future<String, Integer>();
		ret.setFirstResult("hello");
		System.out.println("called method getFirstTupleResult()");
		return ret;
	}

	/**
	 *  Example implementation of the method.
	 */
	public IIntermediateFuture<String> getIntermediateResults() 
	{
		IntermediateFuture<String> ret = new IntermediateFuture<String>();
		ret.addIntermediateResult("hello");
		System.out.println("called method getIntermediateResults()");
		return ret;
	}

}
