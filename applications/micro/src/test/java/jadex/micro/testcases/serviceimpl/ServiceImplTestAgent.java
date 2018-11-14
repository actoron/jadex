package jadex.micro.testcases.serviceimpl;

import org.junit.Ignore;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if service implementations can be omitted when the agent implements them.
 */
@Ignore
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ServiceImplTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	/**
	 *  The agent body.
	 */
	@AgentBody()
	public IFuture<Void> body()
	{
		TestReport tr1 = test(1, PojoProviderAgent.class.getName()+".class");
		TestReport tr2 = test(2, MicroProviderAgent.class.getName()+".class");
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
		return IFuture.DONE;
	}
	
	/**
	 *  Perform a test.
	 *  @param no The test number.
	 *  @param model The agent model.
	 *  @return The test.
	 */
	public TestReport test(int no, String model)
	{
		TestReport tr = new TestReport(""+no, "Test if creating service without explicit implementation works.");
		try
		{
			IExternalAccess exta = agent.createComponent(new CreationInfo(agent.getId()).setFilename(model), null).get();
			IInfoService ser = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IInfoService.class).setProvider(exta.getId())).get();
			String res = ser.getInfo().get();
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setFailed(e);
		}
		return tr;
	}
}
