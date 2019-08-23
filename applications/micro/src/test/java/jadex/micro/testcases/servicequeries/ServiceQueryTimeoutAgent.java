package jadex.micro.testcases.servicequeries;

import java.util.Collection;
import java.util.concurrent.TimeoutException;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.FutureTerminatedException;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
// Todo: long timeouts really necessary?
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 2)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class ServiceQueryTimeoutAgent extends TestAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- methods --------
	
	/**
	 *  Perform tests.
	 */
	protected IFuture<TestReport> test(final IExternalAccess platform, final boolean local)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		IRequiredServicesFeature rsf = agent.getFeature(IRequiredServicesFeature.class);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		final int cnt = 3;
		IComponentIdentifier[] cids = new IComponentIdentifier[cnt];
		
		final TestReport tr = new TestReport("#1", "Test if timeout be used with query");
		
		try
		{
			ISubscriptionIntermediateFuture<IExampleService> queryfut = rsf.addQuery(
				new ServiceQuery<>(IExampleService.class, local? ServiceScope.APPLICATION: ServiceScope.GLOBAL),
				3000);
			
			Future<Void> waitfut = new Future<>();
			
			queryfut.addResultListener(new IIntermediateResultListener<IExampleService>()
			{
				public void exceptionOccurred(Exception exception)
				{
					if(exception instanceof TimeoutException)
					{
						tr.setSucceeded(true);
					}
					else
					{
						tr.setFailed("Wrong exception: "+exception);						
					}
					
					//System.out.println("future terminated");
					waitfut.setResultIfUndone(null);
				}
	
				public void resultAvailable(Collection<IExampleService> results)
				{
					tr.setFailed("Wrong listener method called: resultAvailable().");
					waitfut.setResultIfUndone(null);
				}
				
				public void intermediateResultAvailable(IExampleService result)
				{
					System.out.println("received: "+result+" "+platform.getId().getRoot()+" "+((IService)result).getServiceId().getProviderId().getRoot());
//					System.out.println("thread: " + IComponentIdentifier.LOCAL.get() +" on comp thread: " + agent.getFeature0(IExecutionFeature.class).isComponentThread());
					if(!platform.getId().getRoot().equals(((IService)result).getServiceId().getProviderId().getRoot()))
						System.out.println("Found service that does not come from target platform: "+result);
				}
				
				public void finished()
				{
					tr.setFailed("Wrong listener method called: finished().");
					waitfut.setResultIfUndone(null);
				}
			});

			// The creation info is important to be able to resolve the class/model
			CreationInfo ci = new CreationInfo(agent.getModel().getResourceIdentifier());

			IExternalAccess creator = platform.getId().getPlatformName().equals(agent.getId().getPlatformName()) ? agent : platform;
			for(int i=0; i<cnt; i++)
			{
				
				IFuture<IExternalAccess> fut = creator.createComponent(ci.setFilename(ProviderAgent.class.getName()+".class"));
				cids[i] = fut.get(Starter.getDefaultTimeout(agent.getId()), true).getId();
			}
			
			// Wait for completion of query fut (or some timeout)

			long start = System.currentTimeMillis();
			agent.getFeature(IExecutionFeature.class).waitForDelay(5000, true).thenAccept(v -> 
			{
				if(!tr.isFinished())
					tr.setFailed("query was not terminated in time"); 
				waitfut.setResultIfUndone(null);
			});
			
			waitfut.get();
			queryfut.terminate();
			System.out.println("wait dur: "+(System.currentTimeMillis()-start));
					
//			System.out.println("Correct: could find service: "+ser.getInfo().get());
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed("Problem: could not find service: "+e);
			e.printStackTrace();
		}
		finally
		{
			try
			{
				for(int i=0; i<cids.length; i++)
				{
					platform.getExternalAccess(cids[i]).killComponent().get();
				}
			}
			catch(Exception e)
			{
			}
		}
		
		ret.setResult(tr);
		
		return ret;
	}
}

