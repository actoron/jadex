package jadex.micro.testcases.errorpropagation;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentChildKilled;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;

/**
 *  Test if child killed notification works.
 *  
 *  An agent is terminated when futures of created/body/killed return/throw an exception.
 *  A step exception is only reported except the scheduleStep() result is further used/delegated.
 */
@Agent
@ComponentTypes(@ComponentType(name="child", clazz=ChildAgent.class))
@Configurations(@Configuration(name="def", components=@Component(type="child")))
public class ParentAgent extends JunitAgentTest
{
	/** The agent. */
	@Agent 
	protected IInternalAccess agent;
	
	/** The test reports. */
	protected TestReport[] trs;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	protected void body()
	{
//		System.out.println("Created parent: "+agent.getId());
		
		trs = new TestReport[1];
		trs[0] = new TestReport("#0", "Test if parent is notified when child terminates.");
		agent.waitForDelay(4000).get();
		
		if(!trs[0].isFinished())
			trs[0].setFailed("No child termination received");
		
		agent.killComponent();
	}
	
	/**
	 *  Called when a child component was killed.
	 */
	@AgentChildKilled
	protected void childTerminated(IComponentDescription desc, Exception ex)
	{
		System.out.println("My child component was terminated: "+desc.getName()+" "+desc.getFilename());
		
		trs[0].setSucceeded(true);
		
		// restart behavior
		//agent.createComponent(new CreationInfo().setFilename(desc.getFilename()).setParent(agent.getId()), null);
	}
	
	@AgentKilled
	protected void killed()
	{
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(trs.length, trs));
		agent.killComponent();
	}
}
