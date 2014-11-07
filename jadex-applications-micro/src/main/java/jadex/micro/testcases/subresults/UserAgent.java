package jadex.micro.testcases.subresults;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IntermediateComponentResultListener;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;

@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class,
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="producer", clazz=ResultProducerAgent.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class UserAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		IComponentManagementService cms = (IComponentManagementService)agent.getRequiredService("cms").get();
		IComponentIdentifier cid = cms.createComponent("producer", new CreationInfo(agent.getComponentIdentifier())).getFirstResult();
		IExternalAccess ea = cms.getExternalAccess(cid).get();
		
		final TestReport tr = new TestReport("#1", "Test if intermediate results are retrieved.");
		
		ea.subscribeToResults().addResultListener(agent.getComponentFeature(IExecutionFeature.class).createResultListener(
			new IIntermediateResultListener<Tuple2<String, Object>>()
		{
			boolean ok = false;
			
			public void intermediateResultAvailable(Tuple2<String,Object> result) 
			{
				System.out.println("received: "+result);
				
				// ok if at least one result has been retrieved (should be 5)
				if(result!=null && "res".equals(result.getFirstEntity()))
				{
					ok = true;
				}
			}
			
			public void finished()
			{
				System.out.println("fini");
				
				if(ok)
				{
					tr.setSucceeded(true);
				}
				else
				{
					tr.setFailed("No intermediate results have been retrieved.");
				}
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killAgent();
			}
			
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
				System.out.println("ra: "+result);
				tr.setFailed("No intermediate results have been retrieved: "+result);
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killAgent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				tr.setFailed("Exception occrred: "+exception);
				agent.getComponentFeature(IArgumentsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killAgent();
			}
		}));
	}
}
