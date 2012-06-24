package jadex.bridge.service.component.multiinvoke;

import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.Method;

/**
 *  Simple call distributor.
 *    
 *  It determines:
 *  - which services are called: all services found one time
 *  - with which arguments: all with the same single argument set
 *  - when finished: after each service has been called once
 */
public class SimpleCallDistributor implements ICallDistributor
{
	/** The method. */
	protected Method method;
	
	/** The arguments. */
	protected Object[] args;
	
	/** The results. */
	protected IntermediateFuture<Object> results;
	
	/**
	 *  Create a new distributor.
	 */
	public SimpleCallDistributor(Method method, Object[] args)
	{
		this.method = method;
		this.args = args;
	}
	
	/**
	 *  Start the distributor.
	 */
	public IIntermediateFuture<Object> start()
	{
		results = new IntermediateFuture<Object>();
		return results;
	}
	
	/**
	 *  Add a new service.
	 *  @param service The service.
	 */
	public void addService(Object service)
	{
		performCall(service, getArguments());
	}
	
	/**
	 *  Notify that the search has finished.
	 */
	public void serviceSearchFinished()
	{
		results.setFinished();
	}
	
	/**
	 *  Get the arguments for a call.
	 *  @return The arguments.
	 */
	public Object[] getArguments()
	{
		return args;
	}
	
	/**
	 *  Perform a call on given service with given arguments.
	 */
	public void performCall(Object service, Object[] args)
	{
		try
		{
			Object serres = method.invoke(service, args);
			results.addIntermediateResult(serres);
		}
		catch(Exception e)
		{
			e.printStackTrace();
//			results.setException(e);
		}
	}
}
