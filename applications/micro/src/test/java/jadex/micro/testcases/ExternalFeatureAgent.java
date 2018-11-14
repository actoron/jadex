package jadex.micro.testcases;

import java.util.Map;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.base.test.util.STest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExternalArgumentsResultsFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;

/**
 * 	Tests external features via external access. 
 */
@Agent
@Arguments(@Argument(name="arg1", defaultvalue="\"argval1\"", clazz=String.class))
public class ExternalFeatureAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		TestReport tr1 = new TestReport("#1", "Test if an external feature can be used.");
		
		IExternalArgumentsResultsFeature feat = agent.getExternalAccess().getExternalFeature(IExternalArgumentsResultsFeature.class);
		Map<String, Object> args = feat.getArgumentsAsync().get();
		
		if(args.containsKey("arg1") && "argval1".equals(args.get("arg1")))
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Could not fetch args");
		}
		
		TestReport tr2 = new TestReport("#1", "Test if an external feature from remote platform can be used.");
		
		IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig(getClass())).get();
		
		feat = plat.getExternalFeature(IExternalArgumentsResultsFeature.class);
		args = feat.getArgumentsAsync().get();
		
		if(args.size()>0)
		{
			tr2.setSucceeded(true);
		}
		else
		{
			tr2.setFailed("Could not fetch args");
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, 
			new TestReport[]{tr1, tr2}));
		agent.killComponent();
	}
}
