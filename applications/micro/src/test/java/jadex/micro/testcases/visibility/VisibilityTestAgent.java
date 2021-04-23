package jadex.micro.testcases.visibility;

import java.util.HashMap;
import java.util.Map;

import jadex.base.Starter;
import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.types.cms.CMSStatusEvent.CMSTerminatedEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
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
	//@AgentCreated
	@OnInit
	public IFuture<Void> test()
	{
//		final IExternalAccess plat = Starter.createPlatform(STest.getDefaultTestConfig()).get();
//		Starter.createProxy(agent.getExternalAccess(), plat).get();
//		Starter.createProxy(plat, agent.getExternalAccess()).get();
		final IExternalAccess plat = Starter.createPlatform(getConfig().clone()).get();
		TestAgent.createComponent(agent, FirstAgent.class.getName()+".class", null, null, plat.getId(), plat, null).get();
		TestAgent.createComponent(agent, SecondAgent.class.getName()+".class", null, null, plat.getId(), plat, null).get();
		
		Map<String,Object> args = new HashMap<String, Object>();
		args.put("selfkill", Boolean.TRUE);
		agent.createComponentWithEvents(new CreationInfo(null, args, agent.getModel().getResourceIdentifier()).setFilename(FirstAgent.class.getName()+".class"))
			.next(event ->
		{
			if(event instanceof CMSTerminatedEvent)
			{
				IServiceIdentifier[] sers = (IServiceIdentifier[])((CMSTerminatedEvent)event).getResults().get("found");
//						System.out.println("res: "+Arrays.toString(sers));
				
				plat.killComponent();
				
				TestReport tr1 = new TestReport("#1", "Test provided scope platform is respected.", sers.length == 2, sers.length != 2 ? "Found " + sers.length + " services" : null);

				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr1}));
				agent.killComponent();
			}
		});
		
		return IFuture.DONE;
	}
}
