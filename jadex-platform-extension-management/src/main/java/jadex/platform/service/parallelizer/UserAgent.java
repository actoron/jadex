package jadex.platform.service.parallelizer;

import java.util.Collection;

import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
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
	@RequiredService(name="paser", type=IParallelService.class, binding=@Binding(
		create=true, creationinfo=@CreationInfo(type="pa"))),
})
@ComponentTypes(@ComponentType(name="pa", filename="jadex.platform.service.parallelizer.Par2Agent.class"))
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
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IFuture<IParallelService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("paser");
		fut.addResultListener(new ExceptionDelegationResultListener<IParallelService, Void>(ret)
		{
			public void customResultAvailable(IParallelService paser)
			{
				paser.doParallel(new String[]{"a", "b", "c", "d", "e", "f"})
					.addResultListener(new IIntermediateResultListener<String>()
				{
					public void intermediateResultAvailable(String result)
					{
						System.out.println("ires: "+result);
					}
					
					public void finished()
					{
						System.out.println("fini");
					}
					
					public void resultAvailable(Collection<String> result)
					{
						System.out.println("res: "+result);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						System.out.println("ex: "+exception);
					}
				});
			}
		});
			
		return ret;
	}
}
