package jadex.noplatform.services;

import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IInternalService;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.FutureReturnType;
import jadex.bridge.service.annotation.Raw;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Base implementation for no platform services.
 */
public abstract class BaseService implements IService, IInternalService
{
	protected IComponentIdentifier cid;
	protected IServiceIdentifier sid;
	
	/**
	 *  Create a new base service.
	 */
	public BaseService(IComponentIdentifier cid, Class<?> iface)
	{
		this.cid = cid;
		this.sid = BasicService.createServiceIdentifier(cid, new ClassInfo(iface), new ClassInfo[0], 
			SReflect.getUnqualifiedClassName(iface), null, ServiceScope.PLATFORM, null, false);
	}
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceId()
	{
		return sid;
	}
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public IFuture<Boolean> isValid()
	{
		return new Future<Boolean>(Boolean.TRUE);
	}
		
	/**
	 *  Get the map of properties (considered as constant).
	 *  @return The service property map (if any).
	 */
	@Raw
	public Map<String, Object> getPropertyMap()
	{
		return null;
	}
	
	/**
	 *  todo: support also blackbox args (e.g. byte[]) as args could also use classes that are not available.
	 * 
	 *  Invoke a method reflectively.
	 *  @param methodname The method name.
	 *  @param argtypes The argument types (can be null if method exists only once).
	 *  @param args The arguments.
	 *  @param returntype The future return type if it is a specific future.
	 *  @return The result.
	 */
	public IFuture<Object> invokeMethod(String methodname, ClassInfo[] argtypes, Object[] args, @FutureReturnType ClassInfo returntype)
	{
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Get reflective info about the service methods, args, return types.
	 *  @return The method infos.
	 */
	public IFuture<MethodInfo[]> getMethodInfos()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @return the cid
	 */
	public IComponentIdentifier getComponentId()
	{
		return cid;
	}

	/*@Override
	public IFuture<Void> shutdownService() {
		// TODO Auto-generated method stub
		return null;
	}*/

	
	@Override
	public IFuture<Void> setComponentAccess(IInternalAccess access) 
	{
		// needed for IInternalService
		throw new UnsupportedOperationException();
	}

	@Override
	public void setServiceIdentifier(IServiceIdentifier sid) 
	{
		// needed for IInternalService
		throw new UnsupportedOperationException();
	}
	
	/**
	 *  Create the necessary platform service replacements.
	 *  @return The services (execution and clock).
	 * /
	public static Tuple2<IExecutionService, IClockService> createServices()
	{
		IComponentIdentifier pcid = Starter.createPlatformIdentifier(null);
		IThreadPool threadpool = new JavaThreadPool(true);
		ExecutionService es = new ExecutionService(pcid, threadpool);
		es.startService().get();
		ClockService cs = new ClockService(pcid, null, threadpool);
		cs.startService().get();
		return new Tuple2<IExecutionService, IClockService>(es, cs);
	}*/
}
