package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.commons.Logger;
import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import java.util.Arrays;
import java.util.Map;

import android.content.Intent;
import android.os.IBinder;

/**
 * Android Service to use a single jadex platform.
 * The platform can be launched on creation and will be terminated in onDestroy().
 */
public class JadexPlatformService extends JadexMultiPlatformService implements JadexPlatformOptions, IJadexPlatformBinder
{
	
	private IPlatformConfiguration platformConfiguration;
	private boolean platformAutostart;

	private IComponentIdentifier platformId;

	public JadexPlatformService()
	{
		jadexPlatformManager = JadexPlatformManager.getInstance();
		platformConfiguration = PlatformConfigurationHandler.getAndroidDefault();
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new JadexPlatformBinder(this);
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		if (platformAutostart) {
			startPlatform();
		}
	}

	
	/**
	 * Returns whether the Jadex Platform is or has been started automatically.
	 * @return boolean
	 */
	protected boolean isPlatformAutostart()
	{
		return platformAutostart;
	}

	/**
	 * Sets the autostart parameter for this jadex platform.
	 * If true, the platform will be started during onCreate().
	 * Should be set in constructor, as this is the only method called
	 * before onCreate().
	 * @param autostart
	 */
	protected void setPlatformAutostart(boolean autostart) {
		if (platformId != null || !jadexPlatformManager.isPlatformRunning(platformId)) {
			this.platformAutostart = autostart;
		} else {
			throw new IllegalStateException("Cannot set autostart, platform already running!");
		}
	}
	
	/**
	 * Sets the Kernels.
	 * See {@link JadexPlatformManager} constants for available Kernels.
	 * @param kernels
	 * @deprecated use getPlatformConfiguration().getRootConfig().setKernels() instead.
	 */
	protected void setPlatformKernels(String ... kernels) {
		this.platformConfiguration.getRootConfig().setKernels(kernels);
	}
	
	/**
	 * Returns the platform options of newly created platforms.
	 * @return String[] of options
	 */
//	protected String getPlatformOptions()
//	{
//		return platformConfiguration.getRootConfig().;
//	}

	/**
	 * Sets platform options.
	 * @param options
	 * @deprecated use setPlatformConfiguration
	 */
	protected void setPlatformOptions(String options) {
		this.platformConfiguration.enhanceWith(Starter.processArgs(options));
	}

	/**
	 * Sets platform configuration.
	 * @param config
	 */
	protected void setPlatformConfiguration(IPlatformConfiguration config) {
		this.platformConfiguration = config;
	}

	/**
	 * Get the platform configuration
	 * @return
	 */
	protected IPlatformConfiguration getPlatformConfiguration() {
		return platformConfiguration;
	}

	/**
	 * Returns the name which is used to create the next jadex platform.
	 * @return {@link String} name
	 */
	public String getPlatformName()
	{
		return platformConfiguration.getPlatformName();
	}
	
	/**
	 * Retrieves the platformId of the last started Platform, if any.
	 * @return {@link IComponentIdentifier} platformId or <code>null</code>.
	 */
	public IComponentIdentifier getPlatformId()
	{
		return platformId;
	}

	public void setPlatformId(IComponentIdentifier platformId)
	{
		this.platformId = platformId;
	}

	/**
	 * Sets the name of the platform that is started by this activity.
	 * @param name
	 * @deprecated use getPlatformConfiguration().setPlatformName() instead.
	 */
	protected void setPlatformName(String name) {
		this.platformConfiguration.setPlatformName(name);
	}
	
	final protected IFuture<IExternalAccess> startPlatform()
	{
		Logger.i("Requested kernels: " + Arrays.toString(platformConfiguration.getRootConfig().getKernels()));
		return startJadexPlatform(platformConfiguration);
	}
	
	public IFuture<IComponentManagementService> getCMS()
	{
		return getCMS(platformId);
	}
	
	public IExternalAccess getExternalPlatformAccess()
	{
		return getExternalPlatformAccess(platformId);
	}
	
	public IExternalAccess getPlatformAccess()
	{
		return getExternalPlatformAccess();
	}

	public <S> S getsService(Class<S> serviceClazz)
	{
		return getsService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz)
	{
		return getService(platformId, serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz, String scope)
	{
		return getService(platformId, serviceClazz, scope);
	}

	public void shutdownJadexPlatform()
	{
		shutdownJadexPlatform(platformId);
	}
	

	public IFuture<IComponentIdentifier> startComponent(String name, String modelPath)
	{
		return startComponent(platformId, name, modelPath);
	}
	
	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, String modelPath, CreationInfo creationInfo)
	{
		return startComponent(platformId, name, modelPath, creationInfo);
	}
	
	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, Class<?> clazz)
	{
		return startComponent(platformId, name, clazz);
	}

	@Override
	public IFuture<IComponentIdentifier> startComponent(String name, Class<?> clazz, CreationInfo creationInfo)
	{
		return startComponent(platformId, name, clazz, creationInfo);
	}

	public IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz)
	{
		return startComponent(platformId, name, clazz);
	}
	
	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		this.platformId = platform.getComponentIdentifier();
	}
	

	public boolean isPlatformRunning()
	{
		return isPlatformRunning(platformId);
	}

	// --------------- event -----------------
	
	public void registerEventReceiver(IEventReceiver<?> rec)
	{
		AndroidContextManager.getInstance().registerEventListener(rec);
	}

	public boolean unregisterEventReceiver(IEventReceiver<?> rec)
	{
		return AndroidContextManager.getInstance().unregisterEventListener(rec);
	}

	public boolean dispatchEvent(IJadexAndroidEvent event)
	{
		return AndroidContextManager.getInstance().dispatchEvent(event);
	}
	
	
	//---------------- helper ----------------
	
	/**
	 * Sends a Message to a Component on the Jadex Platform.
	 * 
	 * @param message
	 * @return Future<Void>
	 */
	protected IFuture<Void> sendMessage(final Map<String, Object> message, final IComponentIdentifier receiver)
	{
		checkIfPlatformIsRunning(platformId, "sendMessage");

		return getPlatformAccess().scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				return ia.getComponentFeature(IMessageFeature.class).sendMessage(receiver, message);
			}
		});

	}

}