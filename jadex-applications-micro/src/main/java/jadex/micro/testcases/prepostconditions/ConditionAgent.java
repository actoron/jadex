package jadex.micro.testcases.prepostconditions;

import jadex.bridge.service.annotation.CheckIndex;
import jadex.bridge.service.annotation.CheckNotNull;
import jadex.bridge.service.annotation.CheckState;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Agent
@Service
@ProvidedServices(@ProvidedService(type=IContractService.class, implementation=@Implementation(expression="$pojoagent")))
public class ConditionAgent implements IContractService
{
	@Agent
	MicroAgent agent;
	
	protected int cnt;
	
	/**
	 * 
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		IContractService ts = (IContractService)agent.getServiceContainer().getProvidedServices(IContractService.class)[0];
		
		// a!=null violated
		ts.doSomething(null, 6, 2).addResultListener(new PrintListener<Integer>());
		
		// all ok
		ts.doSomething("hi", 6, 2).addResultListener(new PrintListener<Integer>());
		
		// c>0 violated
		ts.doSomething("hi", 6, -1).addResultListener(new PrintListener<Integer>());

		// result!=null violated
		ts.doSomething("null", 1, 1).addResultListener(new PrintListener<Integer>());

		// result <100 violated
		ts.doSomething("hi", 1000, 1).addResultListener(new PrintListener<Integer>());
		
		
		List<String> names = new ArrayList<String>();
		names.add("Alfons");
		names.add("Berta");
		names.add("Charlie");

		// 
		ts.getName(0, names).addResultListener(new PrintListener<String>());
		ts.getName(1, names).addResultListener(new PrintListener<String>());
		ts.getName(2, names).addResultListener(new PrintListener<String>());

		ts.getName(-1, names).addResultListener(new PrintListener<String>());
		ts.getName(3, names).addResultListener(new PrintListener<String>());
		
		ts.getIncreasingValue().addResultListener(new PrintListener<Collection<Integer>>());

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
	public IFuture<String> getName(@CheckIndex(1) int idx, @CheckNotNull List<String> names)
	{
		return new Future<String>(names.get(idx));
	}
	
	/**
	 * 
	 */
	public @CheckState(value="$res[-1] < $res", intermediate=true) IIntermediateFuture<Integer> getIncreasingValue()
	{
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		ret.addIntermediateResult(new Integer(1));
		ret.addIntermediateResult(new Integer(2));
		ret.addIntermediateResult(new Integer(0));
		
		return ret;
	}

	/**
	 * 
	 */
	public static class PrintListener<E> implements IResultListener<E>
	{
		public void resultAvailable(Object result)
		{
			System.out.println("invoked, result: "+result);
		}
		
		public void exceptionOccurred(Exception exception)
		{
			System.out.println("exception occurred: "+exception);
		}
	}
}
