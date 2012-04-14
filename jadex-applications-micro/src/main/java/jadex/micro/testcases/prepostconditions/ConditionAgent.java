package jadex.micro.testcases.prepostconditions;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.interceptors.ConditionException;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IContractService.class, implementation=@Implementation(expression="$pojoagent")))
public class ConditionAgent implements IContractService
{
	@Agent
	MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IContractService ts = (IContractService)agent.getServiceContainer().getProvidedServices(IContractService.class)[0];
		
		// a!=null violated
		ts.doSomething(null, 6, 2).addResultListener(new PrintListener());
		
		// all ok
		ts.doSomething("hi", 6, 2).addResultListener(new PrintListener());
		
		// c>0 violated
		ts.doSomething("hi", 6, -1).addResultListener(new PrintListener());

		// result!=null violated
		ts.doSomething("null", 1, 1).addResultListener(new PrintListener());

		// result <100 violated
		ts.doSomething("hi", 1000, 1).addResultListener(new PrintListener());
		
		return ret;
	}
	
	/**
	 * 
	 */
	public IFuture<Integer> doSomething(String a, int x, int y)
	{
//		System.out.println("invoked: "+a);
		return "null".equals(a)? new Future(null): new Future<Integer>(new Integer(x/y));
	}

	/**
	 * 
	 */
	public static class PrintListener implements IResultListener<Integer>
	{
		public void resultAvailable(Integer result)
		{
			System.out.println("invoked, result: "+result);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			if(exception instanceof ConditionException)
			{
				System.out.println("condition failed: "+exception);
			}
			else
			{
				System.out.println("Other exception: "+exception);
			}
		}
	}
}
