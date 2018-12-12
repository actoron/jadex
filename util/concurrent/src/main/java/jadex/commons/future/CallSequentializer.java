package jadex.commons.future;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.IResultCommand;
import jadex.commons.Tuple3;

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
	protected List<Tuple3<String, Object[], Future<T>>> calls = new ArrayList<>();
	
	/** The command to execute a call. */
	protected Map<String, IResultCommand<IFuture<T>, Object[]>> commands;
	
	/**
	 *  Create a new sequentializer.
	 */
	public CallSequentializer()
	{
		this.commands = new HashMap<>();
	}
	
	/**
	 *  Create a new sequentializer.
	 *  @param call The command to execute on an invocation.
	 */
	public CallSequentializer(IResultCommand<IFuture<T>, Object[]> call)
	{
		this();
		commands.put(null, call);
	}
	
	/**
	 *  Add a command by (method) name.
	 *  @param method The method name (or just a name).
	 */
	public void addCommand(String method, IResultCommand<IFuture<T>, Object[]> call)
	{
		commands.put(method, call);
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
		return call(null, args);
	}
	
	public IFuture<T> call(String method, Object[] args)
	{
		IResultCommand<IFuture<T>, Object[]> call = commands.get(method); 
		
		if(call==null)
			return new Future<>(new RuntimeException("Method name not found: "+method));
		
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
			calls.add(new Tuple3<String, Object[], Future<T>>(method, args, ret));
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
			Tuple3<String, Object[], Future<T>> next = calls.remove(0);
			IResultCommand<IFuture<T>, Object[]> call = commands.get(next.getFirstEntity()); 
			IFuture<T> fut = call.execute(next.getSecondEntity());
			fut.addResultListener(new DelegationResultListener<T>(next.getThirdEntity()));
			fut.addResultListener(res -> {proceed();}, ex -> {proceed();});
		}
		else
		{
			currentcall = null;
		}
	}
}