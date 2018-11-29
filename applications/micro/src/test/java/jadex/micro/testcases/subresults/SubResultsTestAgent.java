package jadex.micro.testcases.subresults;

import java.util.Collection;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent
@ComponentTypes(@ComponentType(name="producer", clazz=ResultProducerAgent.class))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class SubResultsTestAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		IExternalAccess ea = agent.createComponent(new CreationInfo().setFilename("producer")).get();
		
		final TestReport tr = new TestReport("#1", "Test if intermediate results are retrieved.");
		
		ea.subscribeToResults().addResultListener(agent.getFeature(IExecutionFeature.class).createResultListener(
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
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killComponent();
			}
			
			public void resultAvailable(Collection<Tuple2<String, Object>> result)
			{
				System.out.println("ra: "+result);
				tr.setFailed("No intermediate results have been retrieved: "+result);
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killComponent();
			}
			
			public void exceptionOccurred(Exception exception)
			{
				System.out.println("ex: "+exception);
				tr.setFailed("Exception occrred: "+exception);
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
				agent.killComponent();
			}
		}));
	}
}
