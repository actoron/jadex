package jadex.bridge.service.types.platform;

import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.commons.future.IFuture;

/**
 * Interface to access and control the Jadex platform.
 */
public interface IJadexPlatformInterface
{
	/**
	 * Returns the Jadex External Platform Access object.
	 * 
	 * @param platformID
	 * @deprecated use getPlatformAccess instead.
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess();
	
	/**
	 * Returns the Jadex External Platform Access object.
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getPlatformAccess();

	/**
	 * Returns true if given jadex platform is running.
	 * 
	 */
	public boolean isPlatformRunning();

	/**
	 * Looks up a service and returns it synchronously.
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return the service
	 */
	public <S> S getsService(final Class<S> serviceClazz);
	
	/**
	 * Looks up a service.
	 * 
	 * @see getsService
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return Future of the service.
	 */
	public <S> IFuture<S> getService(final Class<S> serviceClazz);
	
	/**
	 * Looks up a service.
	 * 
	 * @see getsService
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @param scope Search scope. See {@link RequiredServiceInfo} constants.
	 * @return Future of the service.
	 */
	public <S> IFuture<S> getService(final Class<S> serviceClazz, final String scope);
	
	/**
	 * Retrieves the platformId of the last started Platform, if any.
	 * @return {@link IComponentIdentifier} platformId or <code>null</code>.
	 */
	public IComponentIdentifier getPlatformId();


	// ---------- starting / stopping ----------

	/**
	 * Terminates the running jadex platform with the given ID.
	 * 
	 * @param platformID
	 *            Platform to terminate.
	 */
	public void shutdownJadexPlatform();

	// ---------- component creation ----------

	/**
	 * Start a new micro agent on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created agent
	 * @param clazz
	 *            class of the agent to instantiate
	 * @return ComponentIdentifier of the created agent.
	 * 
	 * @deprecated Use startComponent() instead for all component types.
	 */
	public IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz);

	/**
	 * Start a new Component.
	 * 
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created component.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final String modelPath, final CreationInfo creationInfo);


	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @return ComponentIdentifier of the created component.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final String modelPath);
	
	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param name
	 *            name of the newly created component
	 * @param clazz
	 *            Class of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created component.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final Class<?> clazz, final CreationInfo creationInfo);

	/**
	 * Start a new Component on a given platform with default {@link CreationInfo}.
	 * 
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @return ComponentIdentifier of the created component.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final Class<?> clazz);
	
	// ---------- Event-stuff ----------

	/**
	 * Register an event receiver.
	 * @param rec
	 */
	public void registerEventReceiver(IEventReceiver<?> rec);

	/**
	 * Unregister an event receiver.
	 * @param rec
	 * @return true if, the given receiver was registered before removal.
	 */
	public boolean unregisterEventReceiver(IEventReceiver<?> rec);
	
	/**
	 * Dispatch an event.
	 * @param event
	 * @return true, if an receiver was found for this event.
	 * @throws WrongEventClassError
	 */
	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassError;


}
