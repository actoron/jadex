package jadex.micro.testcases.servicescope;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.ITuple2Future;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@RequiredServices(
{
	@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)),
	@RequiredService(name="exaser", type=IExampleService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Arguments(@Argument(name="testcnt", clazz=int.class, defaultvalue="2"))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class UserAgent 
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody(keepalive=false)
	public void body()
	{
		final Testcase tc = new Testcase();
		tc.setTestCount(2);
		
		IComponentManagementService cms = (IComponentManagementService)agent.getServiceContainer().getRequiredService("cms").get();
		
		// Create user as subcomponent -> should be able to find the service with publication scope application
		IComponentIdentifier cid = null;
		TestReport tr = new TestReport("#1", "Test if service with scope application can be found when provider is child of user");
		try
		{
			ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(ProviderAgent.class.getName()+".class", new CreationInfo(agent.getComponentIdentifier()));
			cid = fut.getFirstResult();
			IExampleService ser = (IExampleService)agent.getRequiredService("exaser").get();
//			System.out.println("Correct: could find service: "+ser.getInfo().get());
			tr.setSucceeded(true);
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
				if(cid!=null)
					cms.destroyComponent(cid).get();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		// Create user as subcomponent -> should not be able to find the service with publication scope application
		cid = null;
		tr = new TestReport("#1", "Test if service with scope application can be found when provider is sibling");
		try
		{
			ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(ProviderAgent.class.getName()+".class", new CreationInfo(agent.getModel().getResourceIdentifier()));
			cid = fut.getFirstResult();
			IExampleService ser = (IExampleService)agent.getRequiredService("exaser").get();
			System.out.println("Problem: could find hidden service: "+ser.getInfo().get());
			tr.setFailed("Problem: could find hidden service");
		}
		catch(Exception e)
		{
			System.out.println("Correct: could not find service");
			tr.setSucceeded(true);
//			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(cid!=null)
					cms.destroyComponent(cid).get();
			}
			catch(Exception e)
			{
			}
		}
		tc.addReport(tr);
		
		agent.setResultValue("testresults", tc);
	}
}
