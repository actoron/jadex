package jadex.bridge.service.component.multiinvoke;

import java.lang.reflect.Method;

import jadex.bridge.service.IService;
import jadex.commons.ConstantFilter;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;

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
	
	/** The parameter converter. */
	protected IParameterConverter conv;
	
	/**
	 *  Start the distributor.
	 */
	public IIntermediateFuture<Object> init(Method method, Object[] args, 
		IFilter<Tuple2<IService, Object[]>> filter, IParameterConverter conv)
	{
		this.method = method;
		this.args = args;
		this.filter = filter==null? new ConstantFilter<Tuple2<IService, Object[]>>(true): filter;
		this.conv = conv;
		
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
		return conv==null? args: conv.convertParameters(args);
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
		if(service==null)
			throw new IllegalArgumentException("Service must not be null.");
		
		if(filter.filter(new Tuple2<IService, Object[]>(service, args)))
		{
			return method.invoke(service, args);
		}
		else
		{
			throw new RuntimeException("Filter prohibted call.");
		}
	}
	
//	public static Map<String, Class<? extends IMultiplexDistributor>> mapping;
//	
//	static
//	{
//		mapping = new HashMap<String, Class<? extends IMultiplexDistributor>>();
//		mapping.put(MultiplexDistributor.ONE_TO_ALL, SimpleMultiplexDistributor.class);
//		mapping.put(MultiplexDistributor.ONE_TO_ALL, SequentialMultiplexDistributor.class);
//	}
}
