package jadex.micro.testcases;

import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestInjectedResultsAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr	= new TestReport("#1", "Test if injected results work.");
		
		IFuture<IComponentManagementService> fut = agent.getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				cms.createComponent(null, "jadex/micro/testcases/InjectedResultsAgent.class", 
					new CreationInfo(agent.getComponentIdentifier()), agent.createResultListener(new IResultListener<Collection<Tuple2<String,Object>>>()
				{
					public void resultAvailable(Collection<Tuple2<String, Object>> results)
					{
						Object myres = Argument.getResult(results, "myres");
						Object myint = Argument.getResult(results, "myint");
						
						if("def_val".equals(myres) && new Integer(99).equals(myint))
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Wrong result values: myres="+myres+", myint="+myint);
						}
						
						agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tr.setFailed("Exception occurred: "+exception);
						agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
						ret.setResult(null);
					}
				}));
			}
		});
		
		return ret;
	}
}
