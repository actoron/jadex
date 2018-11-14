package jadex.bdiv3.testcases.misc;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class InitExpressionsBDI
{
	//-------- attributes --------
	
	// Annotation to inform FindBugs that the uninitialized field is not a bug.
	@SuppressFBWarnings(value="UR_UNINIT_READ", justification="Agent field injected by interpreter")

	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The agent name. */
	protected String	name1	= agent.getId().getName();
	
	/** The agent name. */
	protected String	name2;
	
	//-------- constructors --------
	
	/**
	 *  Create the agent.
	 */
	public InitExpressionsBDI()
	{
		this.name2	= agent.getId().getName();
	}
	 
	
	//-------- methods --------
	
	/**
	 *  Agent body.
	 */
	@AgentBody//(keepalive=false)
	public void	body()
	{
		TestReport	tr1	= new TestReport("#1", "Test if field expression works.");
		if(agent.getId().getName().equals(name1))
		{			
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setReason("Values do not match: "+agent.getId().getName()+", "+name1);
		}
		
		TestReport	tr2	= new TestReport("#2", "Test if field expression works.");
		if(agent.getId().getName().equals(name2))
		{			
			tr2.setSucceeded(true);
		}
		else
		{
			tr2.setReason("Values do not match: "+agent.getId().getName()+", "+name2);
		}

		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
	}
}
