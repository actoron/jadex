package jadex.micro.testcases.nfservicetags;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test searching for tagged services.
 */
@Agent
@Service
@RequiredServices({
	@RequiredService(name="testser1", type=ITestService.class),
	@RequiredService(name="testser2", type=ITestService.class, tags=TagProperty.PLATFORM_NAME),
	@RequiredService(name="testser3", type=ITestService.class, tags="blatag")
})

@ComponentTypes(@ComponentType(name="provider", filename="jadex.micro.testcases.nfservicetags.ProviderAgent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="provider")))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class NFServiceTagsTestAgent extends JunitAgentTest
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		final List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr1 = new TestReport("#1", "Test if can find service withouts tags.");
		try
		{
			ITestService ser = (ITestService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("testser1").get();
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);
		
		TestReport tr2 = new TestReport("#2", "Test if can find service with tags in required service defition.");
		try
		{
			ITestService ser = (ITestService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("testser2").get();
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr2.setReason("Exception occurred: "+e);
		}
		results.add(tr2);
		
		TestReport tr3 = new TestReport("#3", "Test if can find service with tags in required service defition that are not defined on service.");
		try
		{
			ITestService ser = (ITestService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("testser3").get();
			tr3.setReason("Found service that does not have the tag");
		}
		catch(Exception e)
		{
			tr3.setSucceeded(true);
		}
		results.add(tr3);
		
		TestReport tr4 = new TestReport("#4", "Test if can find service via SServiceProvider.getServices()");
		try
		{
			Collection<ITestService> sers = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalServices(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_PLATFORM).setServiceTags(TagProperty.PLATFORM_NAME)); 
			tr4.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr4.setReason("Exception occurred: "+e);
		}
		results.add(tr4);
		
		TestReport tr5 = new TestReport("#5", "Test if can find service via SServiceProvider.getService()");
		try
		{
			ITestService ser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_PLATFORM).setServiceTags(TagProperty.PLATFORM_NAME)); 
			tr5.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr5.setReason("Exception occurred: "+e);
		}
		results.add(tr5);
		
		TestReport tr6 = new TestReport("#6", "Test if can find null tagged service service via SServiceProvider.getService()");
		try
		{
			ITestService ser = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ITestService.class, RequiredServiceInfo.SCOPE_PLATFORM).setServiceTags(new String[]{null})); 
			tr6.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr6.setReason("Exception occurred: "+e);
		}
		results.add(tr6);
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
}

