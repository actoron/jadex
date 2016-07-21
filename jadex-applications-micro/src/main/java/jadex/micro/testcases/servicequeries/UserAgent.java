package jadex.micro.testcases.servicequeries;

import java.util.Collection;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Boolean3;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;


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
public class UserAgent 
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(1);
		
		IComponentManagementService cms = (IComponentManagementService)agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
		IRequiredServicesFeature rsf = agent.getComponentFeature(IRequiredServicesFeature.class);
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		final int cnt = 3;
		IComponentIdentifier[] cids = new IComponentIdentifier[cnt];
		final TestReport tr = new TestReport("#1", "Test if ");
		try
		{
			ISubscriptionIntermediateFuture<IExampleService> queryfut = rsf.addQuery(IExampleService.class, RequiredServiceInfo.SCOPE_PLATFORM, null);
			queryfut.addIntermediateResultListener(new IIntermediateResultListener<IExampleService>()
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
				}
				
				public void intermediateResultAvailable(IExampleService result)
				{
					System.out.println("received: "+result);
					num++;
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
			
			for(int i=0; i<cnt; i++)
			{
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(ProviderAgent.class.getName()+".class", new CreationInfo(agent.getComponentIdentifier()));
				cids[i] = fut.getFirstResult();
			}
			
			// Wait some time and then terminate query
			
			agent.getComponentFeature(IExecutionFeature.class).waitForDelay(2000).get();
			
			queryfut.terminate();
			
//			System.out.println("Correct: could find service: "+ser.getInfo().get());
		}
		catch(Exception e)
		{
//			System.out.println("Problem: could not find service");
			tr.setFailed("Problem: could not find service: "+e);
//			e.printStackTrace();
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
		tc.addReport(tr);
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
	}
}
