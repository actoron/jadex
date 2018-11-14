package jadex.micro.testcases.parameterguesser;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IComponentIdentifier;
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
 *  Test if the parameter guesser works correctly when using injection annotations.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class ParameterGuesserTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;

	/**
	 *  The agent body.
	 */
	@AgentBody()
	public IFuture<Void> body()
	{
		TestReport tr1 = test(1, ProviderAgent.class.getName()+".class");
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr1}));
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
		TestReport tr = new TestReport(""+no, " Test if the parameter guesser works correctly when using injection annotations.");
		try
		{
			IComponentIdentifier cid = agent.createComponent(new CreationInfo(agent.getId()).setFilename(model)).getFirstResult();

			IInjectionTestService ser = agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>(IInjectionTestService.class).setProvider(cid)).get();

			Object[] meta = ser.getInjectionClasses();
			Object[] injections = ser.getInjections();
			boolean success = true;
			List<String> errors = new ArrayList<String>();

			for (int i = 0; i < injections.length; i++) {
				Object inj = injections[i];
				if (inj == null) {
					success=false;
					errors.add(meta[i] + " not injected");
				}
			}

			if (success) {
				tr.setSucceeded(true);
			} else {
				StringBuilder sb = new StringBuilder();
				for (String s : errors)
				{
					sb.append(s);
					sb.append("\n");
				}
				tr.setFailed(sb.toString());
			}
		}
		catch(Exception e)
		{
			tr.setFailed(e);
		}
		return tr;
	}
}
