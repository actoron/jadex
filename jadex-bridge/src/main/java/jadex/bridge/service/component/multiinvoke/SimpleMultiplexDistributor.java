package jadex.bridge.service.component.multiinvoke;

import jadex.bridge.service.IService;
import jadex.commons.ConstantFilter;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

import java.lang.reflect.Method;

/**
 *  Simple multiplex call distributor.
 *    
 *  It determines:
 *  - which services are called: all services found one time
 *  - with which arguments: all with the same single argument set
 *  - when finished: after each service has been called once
 */
public class SimpleMultiplexDistributor implements IMultiplexDistributor
{
	/** The method. */
	protected Method method;
	
	/** The arguments. */
	protected Object[] args;
	
	/** The results. */
	protected IntermediateFuture<Object> results;
	
	/** The service filter. */
	protected IFilter<Tuple2<IService, Object[]>> filter;
	
	/** The args. */
	protected Object[] myargs;
	
//	/**
//	 *  Create a new distributor.
//	 */
//	public SimpleMultiplexDistributor()
//	{
//	}
	
	/**
	 *  Start the distributor.
	 */
	public IIntermediateFuture<Object> init(Method method, Object[] args, IFilter<Tuple2<IService, Object[]>> filter)
	{
		this.method = method;
		this.args = args;
		this.filter = filter==null? new ConstantFilter<Tuple2<IService, Object[]>>(true): filter;

		results = new IntermediateFuture<Object>();
		return results;
	}
	
	/**
	 *  Add a new service.
	 *  @param service The service.
	 */
	public void addService(IService service)
	{
		checkPerformCall(service);
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
	 *  Check perform call.
	 */
	public void checkPerformCall(IService service)
	{
		try
		{
			Object res = performCall(service, getArguments());
			results.addIntermediateResult(res);
		}
		catch(Exception e)
		{
			// Simple distributor does not handle service/filter exceptions.
		}
	}
	
	/**
	 *  Perform a call on given service with given arguments.
	 */
	public Object performCall(IService service, Object[] args) throws Exception
	{
		if(filter.filter(new Tuple2<IService, Object[]>(service, args)))
		{
			return method.invoke(service, args);
		}
		else
		{
			throw new RuntimeException("Filter prohibted call.");
		}
	}
}
