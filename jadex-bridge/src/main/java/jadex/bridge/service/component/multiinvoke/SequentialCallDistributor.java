package jadex.bridge.service.component.multiinvoke;

import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Sequential call distributor.
 *    
 *  It determines:
 *  - which services are called: services are called as long as arguments are available
 *  - with which arguments: each argument array once
 *  - when finished: after all arguments have been used
 */
public class SequentialCallDistributor extends SimpleCallDistributor
{
	/** The arguments. */
	protected Iterator<Object[]> args;
	
	/** The list of free services. */
	protected List<Object> freeservices;

	/** The list of busy services. */
	protected List<Object> busyservices;

	/** The waiting for service calls. */
	protected List<Future<Object>> waitingcalls;
	
	/** Flag if service search is finished. */
	protected boolean searchfin;
	
	/**
	 *  Create a new distributor.
	 */
	public SequentialCallDistributor(Method method, List<Object[]> args)
	{
		super(method, null);
		this.freeservices = new ArrayList<Object>();
		this.busyservices = new ArrayList<Object>();
		this.waitingcalls = new ArrayList<Future<Object>>();
		this.args = args.iterator();
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
		freeservices.add(service);
		checkPerformCall(service);
	}
	
	/**
	 *  Notify that the search has finished.
	 */
	public void serviceSearchFinished()
	{
		searchfin = true;
		checkPerformCall(null);
	}
	
	/**
	 *  Get the arguments for a call.
	 *  @return The arguments.
	 */
	public Object[] getArguments()
	{
		return args.next();
	}
	
	/**
	 *  Test if has arguments.
	 *  @return True if has args.
	 */
	public boolean hasArguments()
	{
		return args.hasNext();
	}
	
	/**
	 *  Get the service for a call.
	 *  @return The service.
	 */
	public IFuture<Object> getFreeService()
	{
		final Future<Object> ret = new Future<Object>();
		
		if(!freeservices.isEmpty())
		{
			Object ser = freeservices.remove(0);
			busyservices.add(ser);
			ret.setResult(ser);
		}
		else
		{
			waitingcalls.add(ret);
		}
		
		return ret;
	}
	
	/**
	 *  Check perform call.
	 */
	public void checkPerformCall(Object service)
	{
		if(hasArguments())
		{
			performCall(service!=null? service: getFreeService(), getArguments());
		}
		else
		{
			results.setFinished();
		}
	}
	
	/**
	 *  Perform a call on given service with given arguments.
	 */
	public void performCall(final Object service, Object[] args)
	{
		try
		{
			Object serres = method.invoke(service, args);
			
			if(serres instanceof IFuture)
			{
				((IFuture)serres).addResultListener(new IResultListener()
				{
					public void resultAvailable(Object result) 
					{
						busyservices.remove(service);
						freeservices.add(service);
//						if(searchfin) // should use service directly again or wait for search?!
							checkPerformCall(null);
					}
					
					public void exceptionOccurred(Exception exception)
					{
						resultAvailable(null);
					}
				});
			}
			
			results.addIntermediateResult(serres);
		}
		catch(Exception e)
		{
		}
	}
}
