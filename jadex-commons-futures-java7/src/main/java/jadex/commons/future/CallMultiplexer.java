package jadex.commons.future;

import jadex.bridge.ClassInfo;
import jadex.commons.IResultCommand;
import jadex.commons.Tuple;

import java.util.HashMap;
import java.util.Map;

/**
 * The CallMultiplexer unifies multiple executions of "similar" commands to a single execution.
 * Multiple threads can attempt to call the command. When a "similar" call is already in progress,
 * the thread will wait for the results of the call in progress instead of executing the command
 * itself.
 * Calls are considered "similar" depending on the chosen doCall() method. Similarity can depend
 * simply on the command class itself, a set of key command parameters or both.
 *
 */
public class CallMultiplexer
{
	/** Map of futures of calls that are in progress. */
	protected Map futureMap;
	
	/**
	 *  Instantiates a call multiplexer. The same multiplexer must be shared by
	 *  all "similar" calls to a command.
	 */
	public CallMultiplexer()
	{
		futureMap = new HashMap();
	}
	
	/**
	 *  Calls the command. If a "similar" call is in progress, wait for
	 *  that call's results instead.
	 *  All commands of the same class are considered "similar".
	 *  
	 *  @param call The command.
	 *  @return The results, either from executing the call or "hitchhiking"
	 *  		on a "similar" call.
	 */
	public IFuture doCall(IResultCommand call)
	{
		return doCall(null, call);
	}
	
	/**
	 *  Calls the command. If a "similar" call is in progress, wait for
	 *  that call's results instead.
	 *  All commands of the same class with the same key arguments are considered "similar".
	 *  
	 *  @param keyargs Key argument to differentiate whether calls are similar in addition
	 *  			   to the command class, may be an array.
	 *  @param call The command.
	 *  @return The results, either from executing the call or "hitchhiking"
	 *  		on a "similar" call.
	 */
	public IFuture doCall(Object keyargs, IResultCommand call)
	{
		return doCall(keyargs, call, true);
	}
	
	/**
	 *  Calls the command. If a "similar" call is in progress, wait for
	 *  that call's results instead.
	 *  All commands with the same key arguments and/or command class are considered "similar".
	 *  
	 *  @param keyargs Key argument to differentiate whether calls are similar in addition
	 *  			   to the command class, may be an array.
	 *  @param call The command.
	 *  @param commandaskey Flag whether to consider the command class as part of the key
	 *  					differentiating calls.
	 *  @return The results, either from executing the call or "hitchhiking"
	 *  		on a "similar" call.
	 */
	public IFuture doCall(Object keyargs, IResultCommand call, boolean commandaskey)
	{
//		if(keyargs instanceof String &&  ((String)keyargs).indexOf("KernelComponentAgent.class")!=-1)
//			System.out.println("asfsdggf");
			
		if(keyargs!=null && keyargs.getClass().isArray())
			keyargs = new Tuple((Object[]) keyargs);
		if(commandaskey)
			keyargs = new Tuple(keyargs, new ClassInfo(call.getClass()));
		
		Future ret = (Future)futureMap.get(keyargs);
		if(ret==null)
		{			
//			System.out.println("multiplex create new call for: "+keyargs+" "+futureMap);

			final Object key = keyargs;
			ret = new Future();
			futureMap.put(key, ret);
			((IFuture)call.execute(null)).addResultListener(new DelegationResultListener(ret));
				
			// Todo: result listener on correct thread?
			ret.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
//					System.out.println("eennd: "+result);
					futureMap.remove(key);
				}
				
				public void exceptionOccurred(Exception exception)
				{
//					System.out.println("eexxx: "+exception);
					futureMap.remove(key);
				}
			});
		}
//		else
//		{
//			if(keyargs instanceof Tuple && ((Tuple)keyargs).getEntity(0)!=null)
//				System.out.println("multiplex found call: "+keyargs);
//		}
		return ret;
	}
}
