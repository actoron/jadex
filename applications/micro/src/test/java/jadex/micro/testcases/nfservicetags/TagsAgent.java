package jadex.micro.testcases.nfservicetags;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.sensor.service.TagProperty;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(autoprovide=Boolean3.TRUE)
@Arguments(@Argument(name="tagarg", clazz=Integer.class, defaultvalue="44"))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@Service
public class TagsAgent extends JunitAgentTest implements ITestService2, ITestService3
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
		final List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr1 = new TestReport("#1", "Test if service has tags.");
		try
		{
			IService ser = (IService)agent.getProvidedService(ITestService2.class);
			IServiceIdentifier sid = ser.getServiceId();
			Object val = agent.getNFPropertyValue(sid, TagProperty.NAME).get();
//			System.out.println(val);
			
			if(val instanceof List && ((List)val).size()==4)
				tr1.setSucceeded(true);
			else
				tr1.setReason("Wrong tag values: "+val);
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);
		
		TestReport tr2 = new TestReport("#2", "Test if service has conditional tags.");
		try
		{
			IService ser = (IService)agent.getProvidedService(ITestService3.class);
			IServiceIdentifier sid = ser.getServiceId();
			Object val = agent.getNFPropertyValue(sid, TagProperty.NAME).get();
//			System.out.println(val);
			
			if(val instanceof List && ((List)val).size()==1)
				tr2.setSucceeded(true);
			else
				tr2.setReason("Wrong tag values: "+val);
		}
		catch(Exception e)
		{
			tr2.setReason("Exception occurred: "+e);
		}
		results.add(tr2);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
	
}