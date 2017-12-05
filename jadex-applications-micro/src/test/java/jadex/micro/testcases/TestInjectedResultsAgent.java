package jadex.micro.testcases;

import java.util.Map;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DefaultTuple2ResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test if result back transfer from injected fields works.
 *  Starts a subagent that uses the result injection and
 *  checks if its result values are correct.
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class TestInjectedResultsAgent
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();

		final TestReport tr	= new TestReport("#1", "Test if injected results work.");
		
		IFuture<IComponentManagementService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms");
		fut.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, Void>(ret)
		{
			public void customResultAvailable(IComponentManagementService cms)
			{
				ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(InjectedResultsAgent.class.getName()+".class", 
					new CreationInfo(agent.getComponentIdentifier()));
				fut.addResultListener(new DefaultTuple2ResultListener<IComponentIdentifier, Map<String, Object>>()
				{
					public void firstResultAvailable(IComponentIdentifier result)
					{
					}
					
					public void secondResultAvailable(Map<String, Object> result)
					{
						Object myres = result.get("myres");
						Object myint = result.get("myint");
						
						if("def_val".equals(myres) && Integer.valueOf(99).equals(myint))
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setFailed("Wrong result values: myres="+myres+", myint="+myint);
						}
						
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
						ret.setResult(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						tr.setFailed("Exception occurred: "+exception);
						agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
						ret.setResult(null);
					}
				});
				
//				, agent.getComponentFeature(IExecutionFeature.class).createResultListener(new IResultListener<Collection<Tuple2<String,Object>>>()
//				{
//					public void resultAvailable(Collection<Tuple2<String, Object>> results)
//					{
//						Object myres = Argument.getResult(results, "myres");
//						Object myint = Argument.getResult(results, "myint");
//						
//						if("def_val".equals(myres) && Integer.valueOf(99).equals(myint))
//						{
//							tr.setSucceeded(true);
//						}
//						else
//						{
//							tr.setFailed("Wrong result values: myres="+myres+", myint="+myint);
//						}
//						
//						agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
//						ret.setResult(null);
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						tr.setFailed("Exception occurred: "+exception);
//						agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
//						ret.setResult(null);
//					}
//				}));
			}
		});
		
		return ret;
	}
}
