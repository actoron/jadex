package jadex.micro.testcases.servicesearch;

import java.util.Collection;

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
import jadex.bridge.service.search.MultiplicityException;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;
import jadex.micro.testcases.servicequeries.IExampleService;
import jadex.micro.testcases.servicequeries.ProviderAgent;

@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
// Todo: long timeouts really necessary?
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 2)")}) // cannot use $component.getId() because is extracted from test suite :-(
public class ServiceSearchMultiplicityAgent extends TestAgent
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
		
		final int cnt = 3;
		final int max = cnt-1;
		IComponentIdentifier[] cids = new IComponentIdentifier[cnt];
		
		final TestReport tr = new TestReport("#1", "Test if max <n> services can be found by query");
		
		try
		{
			// The creation info is important to be able to resolve the class/model
			CreationInfo ci = new CreationInfo(agent.getModel().getResourceIdentifier());

			IExternalAccess creator = platform.getId().getPlatformName().equals(agent.getId().getPlatformName()) ? agent : platform;
			for(int i=0; i<cnt; i++)
			{
				IFuture<IExternalAccess> fut = creator.createComponent(ci.setFilename(ProviderAgent.class.getName()+".class"));
				cids[i] = fut.get(Starter.getDefaultTimeout(agent.getId()), true).getId();
			}
			
			ITerminableIntermediateFuture<IExampleService> queryfut = rsf.searchServices(
				new ServiceQuery<>(IExampleService.class, local? ServiceScope.APPLICATION: ServiceScope.GLOBAL)
				.setMultiplicity(new Multiplicity(0, max)));
			
			Future<Void> waitfut = new Future<>();
			
			queryfut.addResultListener(new IIntermediateResultListener<IExampleService>()
			{
				int num = 0;
				public void exceptionOccurred(Exception exception)
				{
					if(exception instanceof MultiplicityException)
					{
						if(num==max)
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Wrong number of results: "+cnt);
						}
					}
					else
					{
						tr.setFailed("Wrong exception: "+exception);						
					}
					
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
					if(platform.getId().getRoot().equals(((IService)result).getServiceId().getProviderId().getRoot()))
					{
						num++;
					}
					else
					{
						System.out.println("Found service that does not come from target platform: "+result);
					}
				}
				
				public void finished()
				{
					tr.setFailed("Wrong listener method called: finished().");
					waitfut.setResultIfUndone(null);
				}
			});

			
			
			// Wait for completion of query fut (or some timeout)

			long start = System.currentTimeMillis();
			if(!queryfut.isDone())
				agent.getFeature(IExecutionFeature.class).waitForDelay(local? 1000: 11000, true).thenAccept(v -> waitfut.setResultIfUndone(null));
			
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

