package jadex.micro.testcases.futureasstream;

import java.util.stream.Stream;

import jadex.bridge.IInternalAccess;
import jadex.bridge.SFuture;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.commons.future.IIntermediateFuture;
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

		return SFuture.streamToFuture(agent, outstream);
	}
}
