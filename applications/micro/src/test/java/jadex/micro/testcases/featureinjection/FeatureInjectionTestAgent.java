package jadex.micro.testcases.featureinjection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that shows how features can be injected.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class FeatureInjectionTestAgent extends JunitAgentTest
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The arguments feature. */
	@AgentFeature
	protected IArgumentsResultsFeature args;
	
	/** The required services feature. */
	@AgentFeature
	protected IRequiredServicesFeature reqs;

	/** The provided services feature. */
	@AgentFeature
	protected IProvidedServicesFeature pros;
	
	/** The execution feature. */
	@AgentFeature
	protected IExecutionFeature exe;

	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		TestReport[] trs = new TestReport[4];
		int cnt = 0;
		
		trs[cnt] = new TestReport("#"+cnt, "Test if arguments injection works");
		if(args!=null)
		{
			trs[cnt++].setSucceeded(true);
		}
		else
		{
			trs[cnt++].setReason("Argument injection nulls.");
		}
		
		trs[cnt] = new TestReport("#"+cnt, "Test if required services injection works");
		if(reqs!=null)
		{
			trs[cnt++].setSucceeded(true);
		}
		else
		{
			trs[cnt++].setReason("Required services injection nulls.");
		}
		
		trs[cnt] = new TestReport("#"+cnt, "Test if provided services injection works");
		if(pros!=null)
		{
			trs[cnt++].setSucceeded(true);
		}
		else
		{
			trs[cnt++].setReason("Provided services injection nulls.");
		}
		
		trs[cnt] = new TestReport("#"+cnt, "Test if execution injection works");
		if(exe!=null)
		{
			trs[cnt++].setSucceeded(true);
		}
		else
		{
			trs[cnt++].setReason("Execution injection nulls.");
		}
		
		System.out.println("args is: "+args);
		System.out.println("reqs is: "+reqs);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.length, trs));
		agent.killComponent();
	}
}
