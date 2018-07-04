package jadex.micro.testcases.syncservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.component.IRequiredServicesFeature;
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
 *  User agent that invokes synchronous service methods.
 */
@Agent
@RequiredServices(@RequiredService(name="syncser", type=ISynchronousExampleService.class))
@ComponentTypes(@ComponentType(name="provider", clazz=ProviderAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="provider")))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class SyncServicesTestAgent extends JunitAgentTest
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
		
		ISynchronousExampleService ser = (ISynchronousExampleService)agent.getComponentFeature(IRequiredServicesFeature.class).getService("syncser").get();
		
		TestReport tr1 = new TestReport("#1", "Test if can use synchronous get with int value.");
		try
		{
			int res = ser.getInt();
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);
		
		TestReport tr2 = new TestReport("#2", "Test if can use synchronous get with collection value.");
		try
		{
			Collection<String> res = ser.getCollection();
			tr2.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr2.setReason("Exception occurred: "+e);
		}
		results.add(tr2);
		
		TestReport tr3 = new TestReport("#3", "Test if can use synchronous void provoking exception.");
		try
		{
			ser.doVoid();
			tr3.setFailed("No exception did occur");;
		}
		catch(ServiceInvalidException e)
		{
			tr3.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr3.setFailed("Wrong exception: "+e);
		}
		results.add(tr3);

		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
}
