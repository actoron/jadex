package jadex.micro.testcases.seqfuture;

import java.util.Collection;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISequenceFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="ts", type=ITestService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
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
		ITestService ts = (ITestService)agent.getServiceContainer().getRequiredService("ts").get();
		ISequenceFuture<String, Integer> fut = ts.getSomeResults();
//		ts.getSomeResults().addResultListener(new IResultListener<Collection<Object>>()
//		{
//			public void resultAvailable(Collection<Object> result)
//			{
//				System.out.println("result: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
		System.out.println("second result: "+fut.getSecondResult());
		System.out.println("first result: "+fut.getFirstResult());
		
//		ts.getSomeResults().addResultListener(new IResultListener<Collection<String>>()
//		{
//			public void resultAvailable(Collection<String> result)
//			{
//				System.out.println("result: "+result);
//			}
//			
//			public void exceptionOccurred(Exception exception)
//			{
//				System.out.println("ex: "+exception);
//			}
//		});
	}
}
