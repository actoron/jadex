package jadex.micro.testcases.futureasstream;

import java.util.ArrayList;
import java.util.List;
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
	
	protected List<String>	results;
	
	@Override
	public IFuture<String> getNextResult()
	{
		String	result	= "result-"+Math.random();
		results.add(result);
		System.out.println("callback executed: "+result);
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
		results	= new ArrayList<String>();
		IExternalAccess	provider	= platform.addComponent(new FutureAsStreamProviderAgent()).get();
		IFutureAsStreamTestService	testservice	= agent.searchService(new ServiceQuery<>(IFutureAsStreamTestService.class).setScope(ServiceScope.GLOBAL)).get();
		
		// Some non-blocking operations on the stream (doesn't trigger processing)
		List<String>	results2	= new ArrayList<String>();
		Stream<String>	stream	= testservice.getSomeResults().asStream().limit(3);
		
		// Blocking forEach (a.k.a. terminal operation)
		System.out.println("before forEach");
		stream.forEach(results2::add);
		System.out.println("after forEach");
		
		// cleanup to avoid interference of local provide with remote test
		provider.killComponent().get();
		
		return new Future<TestReport>(results.equals(results2)
			? new TestReport(local?"#1":"#2", local?"local":"remote", true, null)
			: new TestReport(local?"#1":"#2", local?"local":"remote", false, "Results do not match: "+results+", "+results2));
	}
//	
//	@Override
//	public IPlatformConfiguration getConfig()
//	{
//		return super.getConfig()
//			.getExtendedPlatformConfiguration().setDebugFutures(true)
//			.setSimul(false).setSimulation(false);
//	}
}
