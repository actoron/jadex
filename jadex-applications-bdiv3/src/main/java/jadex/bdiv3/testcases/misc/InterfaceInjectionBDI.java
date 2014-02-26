package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.IBDIAgent;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class InterfaceInjectionBDI implements IBDIAgent
{
	/**
	 *  Agent body.
	 */
	@AgentBody(keepalive=false)
	public void	body(IInternalAccess ia)
	{
		TestReport tr = new TestReport("#1", "Test if interface injection works.");
		System.out.println("my name is: "+getComponentIdentifier());
		if(getAgent()!=null)
		{
			tr.setSucceeded(true);
			getAgent().setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
		}
		else
		{
			tr.setReason("Problem with agent api.");
			ia.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
		}
	}
}
