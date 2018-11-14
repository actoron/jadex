package jadex.micro.testcases.configinheritance;

import java.util.Arrays;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests if a configuration can be inherited and
 *  the super class configuration parts are still available.
 */
@Agent(keepalive=Boolean3.FALSE)
@ComponentTypes(
{	
	@ComponentType(name="emptyc", clazz=EmptyCAgent.class),
})
@Configurations(
{
	@Configuration(name="main", components=
	{
		@Component(type="emptyb", number="2"),
		@Component(type="emptyc")
	}),
	@Configuration(name="same", components=
	{
		@Component(type="emptyb", number="2"),
		@Component(type="emptyc")
	}, replace=true)
})
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SubAgent extends MainAgent
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
//		IComponentDescription[] descs = cms.getChildrenDescriptions(agent.getId()).get();
		IComponentIdentifier[] subs = agent.getChildren(null, null).get();
		
		TestReport tr = new TestReport("#1", "Test if inheritance of a configuration works");
		if(subs.length==4)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong number of subcomponents, expected 4 (1xa, 2xb, 1xc"+" but was: "+Arrays.toString(subs));
		}
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
