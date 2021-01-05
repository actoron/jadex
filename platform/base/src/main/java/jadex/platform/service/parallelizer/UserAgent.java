package jadex.platform.service.parallelizer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 * 
 */
@Agent
@RequiredServices(
{
	@RequiredService(name="paser", type=IParallelService.class),
})
@ComponentTypes(@ComponentType(name="pa", filename="jadex.platform.service.parallelizer.Par2Agent.class"))
@Configurations(@Configuration(name="default", components=@Component(type="pa")))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class UserAgent
{
	//-------- attributes --------
	
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Agent body.
	 */
	//@AgentBody
	@OnStart
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IParallelService> fut = agent.getFeature(IRequiredServicesFeature.class).getService("paser");
		fut.addResultListener(new ExceptionDelegationResultListener<IParallelService, Void>(ret)
		{
			public void customResultAvailable(IParallelService paser)
			{
				String[]	tasks	= new String[]{"a", "b", "c", "d", "e", "f"};
				final Set<String>	todo	= new HashSet<String>(Arrays.asList(tasks));
				
				paser.doParallel(tasks)
					.addResultListener(new IntermediateEmptyResultListener<String>()
				{
					public void intermediateResultAvailable(String result)
					{
						System.out.println("ires: "+result);
						if(!todo.remove(result.substring(11)))	// Strip "result of: "
						{
							ret.setExceptionIfUndone(new RuntimeException("Task performed twice: "+result));
						}
					}
					
					public void finished()
					{
						System.out.println("fini");
						if(todo.isEmpty())
						{
							ret.setResultIfUndone(null);
						}
						else
						{
							ret.setExceptionIfUndone(new RuntimeException("Not all tasks performed: "+todo));
						}
					}
					
					public void resultAvailable(Collection<String> result)
					{
						System.out.println("res: "+result);
						ret.setExceptionIfUndone(new RuntimeException("No intermediate results."));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("ex: "+exception);
						ret.setExceptionIfUndone(exception);
					}
				});
			}
		});
		
		ret.addResultListener(new IResultListener<Void>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
				agent.getFeature(IArgumentsResultsFeature.class).getResults()
					.put("testresults", new Testcase(1, new TestReport[]{
						new TestReport("#1", "Test paralellizer", exception)
					}));
			}
			
			@Override
			public void resultAvailable(Void result)
			{
				agent.getFeature(IArgumentsResultsFeature.class).getResults()
				.put("testresults", new Testcase(1, new TestReport[]{
					new TestReport("#1", "Test paralellizer", true, null)
				}));
			}
		});
			
		return ret;
	}
}
