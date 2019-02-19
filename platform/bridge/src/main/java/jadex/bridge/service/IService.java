package jadex.bridge.service;

import java.util.Map;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.annotation.Raw;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;


/**
 *  The interface for platform services.
 */
@Reference
public interface IService //extends INFMixedPropertyProvider //extends IRemotable INFPropertyProvider, INFMethodPropertyProvider, 
{
	//-------- constants --------
	
	/** Empty service array. */
	public static final IService[] EMPTY_SERVICES = new IService[0];

	//-------- methods --------

	// IMPORTANT: If name is changed, adapt also in BasicServiceInvocationHandler and in RemoteMethodInvocationHandler!
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceId();
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public IFuture<Boolean> isValid();
		
	/**
	 *  Get the map of properties (considered as constant).
	 *  @return The service property map (if any).
	 */
	@Raw
	public Map<String, Object> getPropertyMap();
	
	/**
	 *  Invoke a method reflectively.
	 *  @param methodname The method name.
	 *  @param argtypes The argument types (can be null if method exists only once).
	 *  @param args The arguments.
	 *  @return The result.
	 */
	public IFuture<Object> invokeMethod(String methodname, ClassInfo[] argtypes, Object[] args);

//	/**
//	 *  Get an external interface feature.
//	 *  @param type The interface type of the feature.
//	 *  @return The feature.
//	 */
//	public <T> T getExternalComponentFeature(Class<T> type);
	
	// todo: ?! currently BasicService only has 
//	/**
//	 *  Get the hosting component of the service.
//	 *  @return The component.
//	 */
//	public IFuture<IExternalAccess> getComponent();
}
