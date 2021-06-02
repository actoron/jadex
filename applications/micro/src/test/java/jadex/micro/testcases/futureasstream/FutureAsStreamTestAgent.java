package jadex.micro.testcases.futureasstream;

import java.util.stream.Stream;

import jadex.base.test.TestReport;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.testcases.TestAgent;

/**
 *  Agent that provides a service with a stream.
 */
@Agent
@ProvidedServices(@ProvidedService(type=IFutureAsStreamCallbackService.class, scope = ServiceScope.GLOBAL))
@Service
public class FutureAsStreamTestAgent extends TestAgent	implements	IFutureAsStreamCallbackService
{
	//-------- callback service --------
	
	protected String	results;
	
	@Override
	public IFuture<String> getNextResult()
	{
		double	num	= Math.random();
		String	result	= Double.toString(num);
		results	+= Math.round(num);
		System.out.println("getNextResult: "+result);
		return new Future<>(result);
	}
	
	//-------- test setup --------
	
	/**
	 *  Perform  the test.
	 *  @param cms	The cms of the platform to test (local or remote).
	 * 	@param local	True when tests runs on local platform. 
	 *  @return	The test result.
	 */
	protected IFuture<TestReport> test(IExternalAccess platform, boolean local)
	{
		results	= "";
		IExternalAccess	provider	= platform.addComponent(new FutureAsStreamProviderAgent()).get();
		IExternalAccess	filter	= platform.addComponent(new FutureAsStreamFilterAgent()).get();
		IFutureAsStreamFilterService	testservice	= agent.searchService(new ServiceQuery<>(IFutureAsStreamFilterService.class).setScope(ServiceScope.GLOBAL)).get();
		
		// Some non-blocking operations on the stream (doesn't trigger processing)
		Stream<String>	stream	= testservice.filterResults().asStream().limit(3);
		
		// Non-blocking peek (a.k.a. intermediate operation)
		System.out.println("before peek");
		stream	= stream.peek(str -> System.out.println("peek: "+str));
		agent.waitForDelay(2500).get();
		System.out.println("after peek");
		
		// Blocking forEach (a.k.a. terminal operation)
		System.out.println("before forEach");
		String[]	results2	= new String[]{""};
		stream.forEach(str -> 
		{
			System.out.println("forEach: "+str);
			results2[0]	+= str;
		});
		System.out.println("after forEach");
		
		// cleanup to avoid interference of local provide with remote test
		filter.killComponent().get();
		provider.killComponent().get();
		
		return new Future<TestReport>(results.equals(results2[0])
			? new TestReport(local?"#1":"#2", local?"local":"remote", true, null)
			: new TestReport(local?"#1":"#2", local?"local":"remote", false, "Results do not match: "+results+", "+results2[0]));
	}
	
//	@Override
//	public IPlatformConfiguration getConfig()
//	{
//		return super.getConfig()
//			.getExtendedPlatformConfiguration().setDebugFutures(true)
//			.setSimul(false).setSimulation(false);
//	}
}
