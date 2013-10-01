package jadex.bridge.service.types.platform;

import jadex.android.IEventReceiver;
import jadex.android.exception.WrongEventClassException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.IFuture;

/**
 * Interface for the platform binder object.
 */
public interface IJadexPlatformBinder
{
	/**
	 * Returns the Jadex External Platform Access object for a given platform Id
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID);

	/**
	 * Returns true if given jadex platform is running.
	 * 
	 */
	public boolean isPlatformRunning(IComponentIdentifier platformID);

	/**
	 * Retrieves the CMS of the Platform with the given ID.
	 * 
 	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID);
	
	/**
	 * Retrieves the MS of the Platform with the given ID.
	 * 
	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IMessageService> getMS(IComponentIdentifier platformID);
	
	/**
	 * Looks up a service and returns it synchronously.
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return the service
	 */
	public <S> S getsService(IComponentIdentifier platformId, final Class<S> serviceClazz);
	
	/**
	 * Looks up a service.
	 * 
	 * @see getsService
	 * 
	 * @param platformId Id of the platform to use for lookup
	 * @param serviceClazz Class of the service (interface) to find
	 * @return Future of the service.
	 */
	public <S> IFuture<S> getService(IComponentIdentifier platformId, final Class<S> serviceClazz);
	
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
	public <S> IFuture<S> getService(IComponentIdentifier platformId, final Class<S> serviceClazz, final String scope);
	
	/**
	 * Retrieves the platformId of the last started Platform, if any.
	 * @return {@link IComponentIdentifier} platformId or <code>null</code>.
	 */
	public IComponentIdentifier getPlatformId();


	// ---------- starting / stopping ----------

	/**
	 * Starts a Jadex Platform with default settings.
	 * 
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform();

	/**
	 * Starts a Jadex Platform.
	 * 
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels);

	/**
	 * Starts a Jadex Platform.
	 * 
	 * @param kernels
	 *            String array of kernel Identifiers (see constants in
	 *            {@link JadexPlatformManager}).
	 * @param platformId
	 *            Identifier of the new platform
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId);

	/**
	 * Starts a Jadex Platform.
	 * 
	 * @param kernels
	 *            String array of kernel Identifiers (see constants in
	 *            {@link JadexPlatformManager}).
	 * 
	 * @param platformId
	 *            Identifier of the new platform
	 * @param options
	 *            additional options that are passed directly to the platform
	 *            starter.
	 * 
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform(final String[] kernels, final String platformId, final String options);

	/**
	 * Terminates all running jadex platforms.
	 */
	public void shutdownJadexPlatforms();

	/**
	 * Terminates the running jadex platform with the given ID.
	 * 
	 * @param platformID
	 *            Platform to terminate.
	 */
	public void shutdownJadexPlatform(IComponentIdentifier platformID);

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
	 * @return ComponendIdentifier of the created agent.
	 * 
	 * @deprecated use stsartComponent() for all agent types
	 */
	public IFuture<IComponentIdentifier> startMicroAgent(final IComponentIdentifier platformId, final String name, final Class<?> clazz);


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
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath);

	
	// ---------- Event-stuff ----------

	public void registerEventListener(String eventName, IEventReceiver<?> rec);

	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassException;

	public boolean unregisterEventListener(String eventName, IEventReceiver<?> rec);

}
