package jadex.platform.service.serialization;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.platform.service.servicepool.ICService;
import jadex.transformation.jsonserializer.JsonString;

@Agent(autoprovide = Boolean3.TRUE)
@Results(@Result(name = "testresults", description = "The test result.", clazz = Testcase.class))
@Service
public class JsonStringTest extends JunitAgentTest
{
	/**
	 * The agent.
	 */
	@Agent
	protected IInternalAccess agent;

	/**
	 * The agent body.
	 */
	@AgentBody
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test JsonString serialization.");

		try
		{
			agent.createComponent(new CreationInfo().setFilenameClass(JsonStringAgent.class)).get();

			IJsonStringService service = agent.searchService(new ServiceQuery<>(IJsonStringService.class)).get();
			JsonString jsonString = service.getJsonString().get();

			if (JsonStringAgent.JSON_STRING.equals(jsonString))
			{
				tr.setSucceeded(true);
			} else
			{
				tr.setFailed("JsonString serialization failed; expected: '" + JsonStringAgent.JSON_STRING + "'; actual: '" + jsonString + "';");
			}
		} catch (Exception e)
		{
			tr.setReason("Exception occurred: " + e);
		} finally
		{
			agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
			agent.killComponent();
		}
	}
}
