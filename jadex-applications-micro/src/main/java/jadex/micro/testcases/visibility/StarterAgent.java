package jadex.micro.testcases.visibility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.base.PlatformConfiguration;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.TestAgent;

/**
 *  Simple test agent with one service.
 */
@Results(@Result(name="testcases", clazz=List.class))
@Agent
public class StarterAgent 
{
	@Agent
	protected IInternalAccess agent; 
	
	/**
	 *  Init service method.
	 */
	@AgentCreated
	public IFuture<Void> test()
	{
//		PlatformConfiguration config = PlatformConfiguration.getDefault();
//		config.addComponent(FirstAgent.class.getName()+".class");
//		config.addComponent(SecondAgent.class.getName()+".class");
//		final IExternalAccess plat = jadex.base.Starter.createPlatform(config).get();
		final IExternalAccess plat = TestAgent.createPlatform(agent, null).get();// new String[]{"-component", FirstAgent.class.getName()+".class", "-component", SecondAgent.class.getName()+".class"}).get();
		TestAgent.createComponent(agent, FirstAgent.class.getName()+".class", null, null, plat.getComponentIdentifier(), null);
		TestAgent.createComponent(agent, SecondAgent.class.getName()+".class", null, null, plat.getComponentIdentifier(), null);
		
		IComponentManagementService cms = SServiceProvider.getService(agent, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("selfkill", Boolean.TRUE);
		ITuple2Future<IComponentIdentifier, Map<String, Object>> ag = cms.createComponent(FirstAgent.class.getName()+".class", new CreationInfo(null, args, agent.getModel().getResourceIdentifier()));
		ag.addTuple2ResultListener(null, new IFunctionalResultListener<Map<String,Object>>()
		{
			public void resultAvailable(Map<String, Object> result)
			{
				IServiceIdentifier[] sers = (IServiceIdentifier[])result.get("found");
//				System.out.println("res: "+Arrays.toString(sers));
				
				plat.killComponent();
				
				List<TestReport> tests = new ArrayList<TestReport>();
				tests.add(new TestReport("#1", "Test provided scope platform is respected.", sers.length==2, sers.length!=2? "Found "+sers.length+" services": null));
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testcases", tests);		
				agent.killComponent();
			}
		});
		
		return IFuture.DONE;
	}
}
