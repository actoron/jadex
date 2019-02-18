package jadex.micro.testcases.servicereflection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  The user agent searches services and checks if the results are ok.
 */
//@Agent(keepalive=Boolean3.FALSE)
//@Results(@Result(name="testresults", clazz=Testcase.class))
public class ReflectionInvocationAgent //extends JunitAgentTest
{
//	@Agent
//	protected IInternalAccess agent;
//	
//	/**
//	 *  The agent body.
//	 */
//	@AgentBody
//	public void body()
//	{
//		final Testcase tc = new Testcase();
//		tc.setTestCount(1);
//		
//		// Create user as subcomponent -> should be able to find the service with publication scope application
//		IComponentIdentifier cid = null;
//		TestReport tr = new TestReport("#1", "Test if reflective call works");
//		try
//		{
//			IFuture<IExternalAccess> fut = agent.createComponent(new CreationInfo().setFilename(ProviderAgent.class.getName()+".class"));
//			cid = fut.get().getId();
//			IService ser = (IService)agent.searchService(new ServiceQuery(new ClassInfo("jadex.micro.testcases.servicereflection.IExampleService"))).get();
//			Object result = ser.invokeMethod("jadex.micro.testcases.servicereflection.IExampleService", "add", null, new Object[]{1,2}).get();
//			System.out.println("Got result: "+result);
//			tr.setSucceeded(true);
//		}
//		catch(Exception e)
//		{
////			System.out.println("Problem: could not find service");
//			tr.setFailed("Problem: could not find service: "+e);
////			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				if(cid!=null)
//					agent.getExternalAccess(cid).killComponent().get();
//			}
//			catch(Exception e)
//			{
//			}
//		}
//		tc.addReport(tr);
//		
//		
//		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", tc);
//	}
}
