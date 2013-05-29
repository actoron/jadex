package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class InitExpressionsBDI
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected BDIAgent	agent;
	
	/** The agent name. */
	protected String	name1	= agent.getAgentName();
	
	/** The agent name. */
	protected String	name2;
	
	//-------- constructors --------
	
	/**
	 *  Create the agent.
	 */
	public InitExpressionsBDI()
	{
		this.name2	= agent.getAgentName();
	}
	 
	
	//-------- methods --------
	
	/**
	 *  Agent body.
	 */
	@AgentBody(keepalive=false)
	public void	body()
	{
		TestReport	tr1	= new TestReport("#1", "Test if field expression works.");
		if(agent.getAgentName().equals(name1))
		{			
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setReason("Values do not match: "+agent.getAgentName()+", "+name1);
		}
		
		TestReport	tr2	= new TestReport("#2", "Test if field expression works.");
		if(agent.getAgentName().equals(name2))
		{			
			tr2.setSucceeded(true);
		}
		else
		{
			tr2.setReason("Values do not match: "+agent.getAgentName()+", "+name2);
		}

		agent.setResultValue("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
	}
}
