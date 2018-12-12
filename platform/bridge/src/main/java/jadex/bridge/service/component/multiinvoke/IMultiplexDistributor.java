package jadex.bridge.service.component.multiinvoke;

import java.lang.reflect.Method;

import jadex.bridge.service.IService;
import jadex.commons.IFilter;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateFuture;

/**
 *  Interface for multiplex call distributor.
 *  
 *  It is fed with:
 *  - the services found one by one (addService)
 *  - when the search has finished (serviceSearchFinished) 
 *  
 *  It determines:
 *  - which services are called
 *  - with which arguments
 *  - when finished
 */
public interface IMultiplexDistributor
{
	/**
	 *  Init the call distributor.
	 */
	public IIntermediateFuture<Object> init(Method method, Object[] args, 
		IFilter<Tuple2<IService, Object[]>> filter, IParameterConverter conv);
	
	/**
	 *  Add a new service.
	 *  @param service The service.
	 */
	public void addService(IService service);
	
	/**
	 *  Search for services has finished.
	 */
	public void serviceSearchFinished();
	
}
