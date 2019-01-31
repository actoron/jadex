package jadex.micro.testcases.nfservicetags;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentServiceSearch;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent//(autoprovide=Boolean3.TRUE)
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@Service
public class TagsAgent //implements ITestService2 //extends JunitAgentTest 
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	// tools not in cp :-(
//	public TagsAgent() 
//    {
//		super();
//		getConfig().setGui(true);
//    }
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		System.out.println("hallo");
		/*final List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr1 = new TestReport("#1", "Test if service has tags.");
		try
		{
			IService ser = (IService)agent.getProvidedService(ITestService2.class);
			IServiceIdentifier sid = ser.getServiceId();
			Object val = agent.getNFPropertyValue(sid, TagProperty.NAME);
			System.out.println(val);
			
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);*/
	}
	
	@AgentKilled
	public void end()
	{
		System.out.println("terminated");
	}
}
