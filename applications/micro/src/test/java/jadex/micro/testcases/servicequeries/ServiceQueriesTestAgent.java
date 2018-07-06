package jadex.micro.testcases.servicequeries;

import java.util.Collection;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.IService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;


/**
 *  The user agent uses service queries. 
 */
@Agent(keepalive=Boolean3.FALSE)
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	//@RequiredService(name="exaser", type=IExampleService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Results(@Result(name="testresults", clazz=Testcase.class))
// Todo: long timeouts really necessary?
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledLocalDefaultTimeout(null, 2)")}) // cannot use $component.getIdentifier() because is extracted from test suite :-(
public class ServiceQueriesTestAgent extends TestAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	//-------- methods --------
	
	/**
	 *  Perform tests.
	 */
	protected IFuture<TestReport> test(final IComponentManagementService cms, final boolean local)
	{
		final Future<TestReport> ret = new Future<TestReport>();
		
		IRequiredServicesFeature rsf = agent.getFeature(IRequiredServicesFeature.class);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		final int cnt = 3;
		IComponentIdentifier[] cids = new IComponentIdentifier[cnt];
		final TestReport tr = new TestReport("#1", "Test if ");
		try
		{
			ISubscriptionIntermediateFuture<IExampleService> queryfut = rsf.addQuery(new ServiceQuery<>(IExampleService.class, local? RequiredServiceInfo.SCOPE_PLATFORM: RequiredServiceInfo.SCOPE_GLOBAL));
			queryfut.addResultListener(new IIntermediateResultListener<IExampleService>()
			{
				int num = 0;
				public void exceptionOccurred(Exception exception)
				{
					finished();
					//tr.setReason(exception.getMessage());
				}
	
				public void resultAvailable(Collection<IExampleService> results)
				{
					for(IExampleService res: results)
					{
						intermediateResultAvailable(res);
					}
					finished();
				}
				
				public void intermediateResultAvailable(IExampleService result)
				{
					System.out.println("received: "+result+" "+cms.getRootIdentifier().get()+" "+((IService)result).getServiceIdentifier().getProviderId().getRoot());
					System.out.println("thread: " + IComponentIdentifier.LOCAL.get() +" on comp thread: " + agent.getFeature0(IExecutionFeature.class).isComponentThread());
					if(cms.getRootIdentifier().get().equals(((IService)result).getServiceIdentifier().getProviderId().getRoot()))
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
					if(num==cnt)
					{
						tr.setSucceeded(true);
					}
					else
					{
						tr.setFailed("Wrong number of results: "+cnt);
					}
				}
			});

			// The creation info is important to be able to resolve the class/model
			CreationInfo ci = ((IService)cms).getServiceIdentifier().getProviderId().getPlatformName().equals(agent.getIdentifier().getPlatformName())
				? new CreationInfo(agent.getIdentifier(), agent.getModel().getResourceIdentifier()) : new CreationInfo(agent.getModel().getResourceIdentifier());

			for(int i=0; i<cnt; i++)
			{
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(ProviderAgent.class.getName()+".class", ci);
				cids[i] = fut.getFirstResult(Future.UNSET, true);
			}
			
			// Wait some time and then terminate query
			
			long start = System.currentTimeMillis();
			agent.getFeature(IExecutionFeature.class).waitForDelay(local? 1000: 11000, true).get();
			System.out.println("wait dur: "+(System.currentTimeMillis()-start));
			
			queryfut.terminate();
			
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
					cms.destroyComponent(cids[i]).get();
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

