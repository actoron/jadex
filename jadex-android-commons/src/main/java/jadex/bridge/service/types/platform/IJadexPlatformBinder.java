package jadex.bridge.service.types.platform;

import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.IFuture;

/**
 * Interface for the platform binder object.
 */
public interface IJadexPlatformBinder extends IJadexMultiPlatformBinder
{
	/**
	 * Returns the Jadex External Platform Access object for a given platform Id
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess();

	/**
	 * Returns true if given jadex platform is running.
	 * 
	 */
	public boolean isPlatformRunning();

	/**
	 * Retrieves the CMS of the Platform with the given ID.
	 * 
 	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IComponentManagementService> getCMS();
	
	/**
	 * Retrieves the MS of the Platform with the given ID.
	 * 
	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IMessageService> getMS();
	
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

	// ---------- agent creation ----------

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
	 * @deprecated Use startComponent() instead for all agent types.
	 */
	public IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz);

	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final String modelPath, final CreationInfo creationInfo);


	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created agent
	 * @param modelPath
	 *            Path to the bpmn model file of the new agent
	 * @return ComponendIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final String modelPath);
	
	/**
		/**
	 * Start a new Component on a given platform.
	 * 
	 * @param name
	 *            name of the newly created component
	 * @param clazz
	 *            Class of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final Class<?> clazz, final CreationInfo creationInfo);

	/**
	 * Start a new Component on a given platform with default {@link CreationInfo}.
	 * 
	 * @param name
	 *            name of the newly created agent
	 * @param modelPath
	 *            Path to the bpmn model file of the new agent
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final String name, final Class<?> clazz);

	
	// ---------- Event-stuff ----------

	public void registerEventReceiver(IEventReceiver<?> rec);

	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassError;

	public boolean unregisterEventReceiver(IEventReceiver<?> rec);

}
