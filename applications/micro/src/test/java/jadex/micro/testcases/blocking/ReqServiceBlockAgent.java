package jadex.micro.testcases.blocking;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test threaded component execution.
 */
@Agent(keepalive=Boolean3.FALSE)
@Service
@Results(@Result(name="testresults", clazz=Testcase.class))
@ProvidedServices(@ProvidedService(type=IBlockService.class))
@RequiredServices(
{
	@RequiredService(name="myser", type=IBlockService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_LOCAL)),
	@RequiredService(name="stepser", type=IStepService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))
})
public class ReqServiceBlockAgent  extends JunitAgentTest implements IBlockService
{
	/**
	 *  Execute the agent
	 */
	@AgentBody
	public void	execute(final IInternalAccess agent)
	{
		TestReport[] trs = new TestReport[2];
		
		trs[0] = new TestReport("#1", "Test if required service can be fetched with get()");
		try
		{
			IBlockService bs = (IBlockService)agent.getFeature(IRequiredServicesFeature.class).getService("myser").get(1000);
			trs[0].setSucceeded(true);
		}
		catch(Exception e)
		{
			trs[0].setFailed("Exception occurred: "+e.getMessage());
			e.printStackTrace();
		}
		
		trs[1] = new TestReport("#2", "Test if not available required service can be fetched with get()");
		try
		{
			IStepService ss = (IStepService)agent.getFeature(IRequiredServicesFeature.class).getService("stepser").get(10);
			trs[1].setFailed("Non-available service found: "+ss.toString());
		}
		catch(Exception e)
		{
			trs[1].setSucceeded(true);
//			e.printStackTrace();
		}
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, trs));
	}
	
	/**
	 *  Block until the given time has passed.
	 */
	public IFuture<Void> block(long millis)
	{
		return IFuture.DONE;
	}
}
