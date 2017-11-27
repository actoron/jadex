package jadex.bridge.service.types.platform;

import java.util.Map;

import jadex.base.IPlatformConfiguration;
import jadex.base.IRootComponentConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 * Interface for the platform binder object.
 */
public interface IJadexMultiPlatformBinder
{
	/**
	 * Returns the Jadex External Platform Access object for a given platform Id
	 * 
	 * @param platformID
	 * @return IExternalAccess
	 */
	public IExternalAccess getExternalPlatformAccess(IComponentIdentifier platformID);

	/**
	 * Checks whether a jadex platform is running.
	 * 
	 * @param platformID the IComponenntIdentifier of the platform to check.
	 * @return true, when runnning, else false.
	 */
	public boolean isPlatformRunning(IComponentIdentifier platformID);

	/**
	 * Retrieves the CMS of the Platform with the given ID.
	 * 
 	 * @deprecated use getService() or getsService() instead.
	 */
	public IFuture<IComponentManagementService> getCMS(IComponentIdentifier platformID);
	
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
	

	// ---------- starting / stopping ----------

	/**
	 * Starts a Jadex Platform with default settings.
	 * 
	 * @return IFuture<IExternalAccess> The external platform access
	 */
//	public IFuture<IExternalAccess> startJadexPlatform();

	/**
	 * Starts a Jadex Platform.
	 * @param kernels
	 *            String array of kernel Identifiers (see constants in
	 *            {@link JadexPlatformManager}).
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
	 * Starts a Jadex Platform with default configuration
	 *
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform();

	/**
	 * Starts a Jadex Platform.
	 * 
	 * @param config
	 *            additional options that are passed directly to the platform
	 *            starter.
	 * 
	 * @return IFuture<IExternalAccess> The external platform access
	 */
	public IFuture<IExternalAccess> startJadexPlatform(final IPlatformConfiguration config);

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
	 * Start a new Component on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @param terminationListener
	 * 			  The listener to call when the component was terminated.
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath, final CreationInfo creationInfo, final IResultListener<Map<String,Object>> terminationListener);
	
	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath, final CreationInfo creationInfo);

	/**
	 * Start a new Component on a given platform with default {@link CreationInfo}.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created component
	 * @param modelPath
	 *            Path to the model file of the new component
	 * @return ComponendIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final String modelPath);
	
	/**
	 * Start a new Component on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created component
	 * @param clazz
	 *            Class of the new component
	 * @param creationInfo
	 * 			  {@link CreationInfo} to pass to the started Component.
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final Class<?> clazz, final CreationInfo creationInfo);

	/**
	 * Start a new Component on a given platform with default {@link CreationInfo}.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created agent
	 * @param clazz
	 *            Path to the bpmn model file of the new agent
	 * @return ComponentIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startComponent(final IComponentIdentifier platformId, final String name, final Class<?> clazz);
	
	/**
	 * Returns the ResourceIdentifier of the /ClientApp/ classes.
	 * As one JadexService exists per ClientApp, this will never change.
	 * Use this RID to launch new components that are defined within the ClientApp.
	 *  
	 * @return
	 */
	public IResourceIdentifier getResourceIdentifier();
	
}
