package jadex.micro.testcases.futureasstream;

import java.util.stream.Stream;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnService;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;

/**
 *  Filter intermediate results using java 8 stream API.
 */
@ProvidedServices(@ProvidedService(type=IFutureAsStreamFilterService.class, scope = ServiceScope.GLOBAL))
@Service(IFutureAsStreamTestService.class)
@Agent
public class FutureAsStreamFilterAgent implements IFutureAsStreamFilterService
{
	@OnService(required = Boolean3.TRUE, requiredservice = @RequiredService(scope = ServiceScope.GLOBAL))
	protected IFutureAsStreamTestService	provider;
	
	@Agent
	protected IInternalAccess	agent;
	
	@Override
	public IIntermediateFuture<String> filterResults()
	{
		IIntermediateFuture<String>	future	= provider.getSomeResults();
		Stream<String>	instream	= future.asStream();

		// Take string containing random value between 0..1 and convert to "0" or "1". 
		Stream<String>	outstream	= instream
			.peek(str -> System.out.println("filterResults0: "+str))
			.map(numstr -> Double.parseDouble(numstr))	// To double
			.map(num -> Math.round(num))				// To nearest long
			.map(num -> num.toString())					// back to string
			.peek(str -> System.out.println("filterResults1: "+str));

		return streamToFuture(outstream);
	}

	/**
	 *  Create an intermediate future for a stream.
	 *  The results are pulled from the stream using the agent thread i.e. the agent will be blocked when waiting for stream results.
	 *  Safe to use (but somewhat useless) for finished streams.
	 *  Also safe to use for streams, created with IntermediateFuture.asStream().
	 *  Not safe to use for other kinds of infinite streams!
	 */
	protected IntermediateFuture<String> streamToFuture(Stream<String> results)
	{
		// Asynchronously transform results, otherwise method would block before returning stream-connected future
		// and results would only be sent in bunch at the end or never, if the source future doesn't finish.
		IntermediateFuture<String>	ret	= new IntermediateFuture<String>();
		agent.scheduleStep(ia ->
		{
			results.forEach(str -> ret.addIntermediateResult(str));
			ret.setFinished();
			return IFuture.DONE;
		});
		return ret;
	}
}
