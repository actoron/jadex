package jadex.micro.testcases.visibility;

import java.util.HashMap;
import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.base.test.util.STest;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.types.cms.CreationInfo;
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
@Results(@Result(name="testresults", clazz=Testcase.class))
@Agent
public class VisibilityTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent; 
	
	/**
	 *  Init service method.
	 */
	@AgentCreated
	public IFuture<Void> test()
	{
//		final IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig()).get();
//		Starter.createProxy(agent.getExternalAccess(), plat).get();
//		Starter.createProxy(plat, agent.getExternalAccess()).get();
		final IExternalAccess plat = STest.createPlatform();
		TestAgent.createComponent(agent, FirstAgent.class.getName()+".class", null, null, plat.getId(), null).get();
		TestAgent.createComponent(agent, SecondAgent.class.getName()+".class", null, null, plat.getId(), null).get();
		
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("selfkill", Boolean.TRUE);
		ITuple2Future<IComponentIdentifier, Map<String, Object>> ag = agent.createComponent(new CreationInfo(null, args, agent.getModel().getResourceIdentifier()).setFilename(FirstAgent.class.getName()+".class"));
		ag.addTuple2ResultListener(null, new IFunctionalResultListener<Map<String,Object>>()
		{
			public void resultAvailable(Map<String, Object> result)
			{
				IServiceIdentifier[] sers = (IServiceIdentifier[])result.get("found");
//				System.out.println("res: "+Arrays.toString(sers));
				
				plat.killComponent();
				
				TestReport tr1 = new TestReport("#1", "Test provided scope platform is respected.", sers.length == 2, sers.length != 2 ? "Found " + sers.length + " services" : null);

				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr1}));
				agent.killComponent();
			}
		});
		
		return IFuture.DONE;
	}
}
