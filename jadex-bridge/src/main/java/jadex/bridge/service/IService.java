package jadex.bridge.service;

import jadex.bridge.nonfunctional.INFPropertyMetaInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.commons.future.IFuture;

import java.util.Map;


/**
 *  The interface for platform services.
 */
@Reference
public interface IService //extends IRemotable
{
	//-------- constants --------
	
	/** Empty service array. */
	public static final IService[] EMPTY_SERVICES = new IService[0];

	//-------- methods --------

	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier();
	
	/**
	 *  Test if the service is valid.
	 *  @return True, if service can be used.
	 */
	public IFuture<Boolean> isValid();
	
	/**
	 *  Returns the names of all non-functional properties of this service.
	 *  
	 *  @return The names of the non-functional properties of this service.
	 */
	public IFuture<String[]> getNonFunctionalPropertyNames();
	
	/**
	 *  Returns the meta information about a non-functional property of this service.
	 *  
	 *  @param name Name of the property.
	 *  @return The meta information about a non-functional property of this service.
	 */
	public IFuture<INFPropertyMetaInfo> getNfPropertyMetaInfo(String name);
	
	/**
	 *  Returns the current value of a non-functional property of this service.
	 *  
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public<T extends Object> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type);
	
	/**
	 *  Returns the current value of a non-functional property of this service, performs unit conversion.
	 *  
	 *  @param name Name of the property.
	 *  @param type Type of the property value.
	 *  @param unit Unit of the property value.
	 *  @return The current value of a non-functional property of this service.
	 */
	public<T extends Object, U extends Object> IFuture<T> getNonFunctionalPropertyValue(String name, Class<T> type, Class<U> unit);
	
	/**
	 *  Get the map of properties (considered as constant).
	 *  @return The service property map (if any).
	 */
	public Map<String, Object> getPropertyMap();

	// todo: ?! currently BasicService only has 
//	/**
//	 *  Get the hosting component of the service.
//	 *  @return The component.
//	 */
//	public IFuture<IExternalAccess> getComponent();
}
