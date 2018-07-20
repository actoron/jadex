package jadex.commons.future;

import java.util.ArrayList;
import java.util.List;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple2;

/**
 *  The call sequentializer realizes a 'critical region' for async calls,
 *  i.e. it sequentializes access to a method.
 *  
 *  - It uses a command to call the method
 *  - It guarantees first come first served method invocation.
 *  
 */
public class CallSequentializer<T>
{
	//int cnt = 0;
	
	/** The current call. */
	protected IFuture<T> currentcall;
	
	/** The list of waiting calls with future and args. */
	protected List<Tuple2<Object[], Future<T>>> calls = new ArrayList<>();
	
	/** The command to execute a call. */
	protected IResultCommand<IFuture<T>, Object[]> call;
	
	/**
	 *  Create a new sequentializer.
	 *  @param call The command to execute on an invocation.
	 */
	public CallSequentializer(IResultCommand<IFuture<T>, Object[]> call)
	{
		this.call = call;
	}
	
	/**
	 *  Handles a call.
	 *  
	 *  If no other call is in it will execute the command.
	 *  
	 *  If at least one call is in it will be added to the calls list.
	 *  
	 *  When a call finishes the next is fetched from the list until empty.
	 *  
	 *  @param args The call args.
	 *  @return The call result future.
	 */
	public IFuture<T> call(Object[] args)
	{
		//cnt++;
		if(currentcall==null)
		{
			currentcall = call.execute(args);
			currentcall.addResultListener(res -> {proceed();}, ex -> {proceed();});
			return currentcall;
		}
		else
		{
			Future<T> ret = new Future<>();
			calls.add(new Tuple2<Object[], Future<T>>(args, ret));
			return ret;
		}
	}
	
	/**
	 *  Called when a command returns.
	 */
	protected void proceed()
	{
		//cnt--;
		//System.out.println("call count: "+cnt);
		if(calls.size()>0)
		{
			Tuple2<Object[], Future<T>> next = calls.remove(0);
			IFuture<T> fut = call.execute(next.getFirstEntity());
			fut.addResultListener(new DelegationResultListener<T>(next.getSecondEntity()));
			fut.addResultListener(res -> {proceed();}, ex -> {proceed();});
		}
		else
		{
			currentcall = null;
		}
	}
}