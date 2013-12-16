package jadex.micro.testcases.configinheritance;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Arrays;

/**
 *  Agent that tests if a configuration can be inherited and
 *  the super class configuration parts are still available.
 */
@Agent
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
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class SubAgent extends MainAgent
{
	/**
	 *  Execute the agent
	 */
	@AgentBody(keepalive=false)
	public void	execute(final IInternalAccess agent)
	{
		IComponentManagementService cms = (IComponentManagementService)agent.getServiceContainer().getRequiredService("cms").get();
		IComponentDescription[] descs = cms.getChildrenDescriptions(agent.getComponentIdentifier()).get();
		
		TestReport tr = new TestReport("#1", "Test if inheritance of a configuration works");
		if(descs.length==4)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong number of subcomponents, expected 4 (1xa, 2xb, 1xc"+" but was: "+Arrays.toString(descs));
		}
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
