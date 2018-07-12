package jadex.android;

import jadex.android.exception.JadexAndroidError;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.android.exception.WrongEventClassError;
import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.bridge.service.types.platform.IJadexPlatformInterface;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.util.Map;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Window;

/**
 * This is an Android Activity Class which provides needed Functionality and
 * comfort Features for Jadex Android Activities. It uses the {@link JadexPlatformService}
 * internally, but takes care about service binding.
 * <br>
 * <b>IMPORTANT</b>: When creating real applications, you need to use your own Service (extend JadexPlatformService)!
 * <br>
 * <b>Don't use this Activity for real applications as the service will be killed when it's destroyed.</b>
 * 
 * @author Julian Kalinowski
 */
public class JadexAndroidActivity extends Activity implements ServiceConnection, IJadexPlatformInterface
{
	private Intent serviceIntent;
	private IJadexPlatformBinder platformService;
	protected IComponentIdentifier platformId;
	
	private boolean platformAutostart;
	private IPlatformConfiguration platformConfiguration;

	/**
	 * Constructor
	 */
	public JadexAndroidActivity()
	{
		super();
		platformAutostart = false;
		platformConfiguration = PlatformConfigurationHandler.getAndroidDefault();
	}
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		try
		{
			serviceIntent = new Intent(this, SReflect.classForName("jadex.android.service.JadexPlatformService", this.getClass().getClassLoader()));
		}
		catch (ClassNotFoundException e)
		{
			throw new JadexAndroidError("Class JadexPlatformService not found. Did you include the library jadex-platform-android in your build?");
		}
		boolean result = bindService(serviceIntent, this, Service.BIND_AUTO_CREATE);
		if (!result) {
			throw new JadexAndroidError("Could not bind JadexPlatformService. Please include the service declaration in your manifest like this: \n <service android:name=\"jadex.android.service.JadexPlatformService\" android:label=\"JadexPlatformService\"/>");
		}
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}

	/**
	 * Sets the autostart parameter for this jadex platform.
	 * If true, the platform will be started during onCreate.
	 * @param autostart
	 */
	protected void setPlatformAutostart(boolean autostart) {
		if (!isPlatformRunning()) {
			this.platformAutostart = autostart;
		} else {
			throw new IllegalStateException("Cannot set autostart, platform already running!");
		}
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
	
//	/**
//	 * Sets the Kernels.
//	 * See {@link jadex.base.IRootComponentConfiguration.KERNELS} Constants for available Kernels.
//	 * @param kernels
//	 * @deprecated use getPlatformConfiguration().getRootConfig().setKernels() instead.
//	 */
//	protected void setPlatformKernels(String ... kernels) {
//		this.platformConfiguration.setKernels(kernels);
//	}
//	
//	/**
//	 * Sets platform options.
//	 * @param options
//	 * @deprecated use setPlatformConfiguration
//	 */
//	protected void setPlatformOptions(String options) {
//		this.platformConfiguration.enhanceWith(Starter.processArgs(options));
//	}
//	
	/**
	 * Sets the name of the platform that is started by this activity.
	 * @param name
	 * @deprecated use getPlatformConfiguration().setPlatformName() instead.
	 */
	protected void setPlatformName(String name) {
		this.platformConfiguration.setPlatformName(name);
	}

	public boolean isPlatformRunning()
	{
		if (platformService != null) {
			return platformService.isPlatformRunning(platformId);
		} else {
			return false;
		}
	}
	
	public IComponentIdentifier getPlatformId() {
		checkIfJadexIsRunning("getPlatformId");
		return platformId;
	};
	
	
	
	public IExternalAccess getPlatformAccess() {
		checkIfJadexIsRunning("getPlatformAccess()");
		return platformService.getExternalPlatformAccess(platformId);
	}
	
	public IExternalAccess getExternalPlatformAccess()
	{
		return getPlatformAccess();
	}

	/**
	 * Gets the platform service.
	 * @return PlatformService binder
	 */
	protected IJadexPlatformBinder getPlatformService() {
		return platformService;
	}
	
	public <S> S getsService(Class<S> serviceClazz)
	{
		checkIfJadexIsRunning("getsService()");
		return platformService.getsService(serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz)
	{
		checkIfJadexIsRunning("getService()");
		return platformService.getService(serviceClazz);
	}

	public <S> IFuture<S> getService(Class<S> serviceClazz, String scope)
	{
		checkIfJadexIsRunning("getService()");
		return platformService.getService(serviceClazz, scope);
	}

	protected boolean isPlatformRunning(IComponentIdentifier platformId)
	{
		if (platformService != null) {
			return platformService.isPlatformRunning(platformId);
		} else {
			return false;
		}
	}

	/**
	 * Starts a Micro Agent.
	 * @param name Name of the Micro Agent created
	 * @param clazz Class which defines the Micro Agent
	 * @return IFuture<IComponentIdentifier>
	 */
	public IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz)
	{
		checkIfJadexIsRunning("startMicroAgent()");
		return platformService.startMicroAgent(name, clazz);
	}
	
	/**
	 * Starts a Component.
	 * @param name Name of the Component created
	 * @param modelPath Path to the Component XML definition file
	 * @deprecated Use startComponent for all Component types instead.
	 * @return IFuture<IComponentIdentifier>
	 */
	protected IFuture<IComponentIdentifier> startBDIAgent(final String name, final String modelPath) {
		return startComponent(name, modelPath);
	}
	
	/**
	 * Starts a Component.
	 * @param name Name of the Component created
	 * @param modelPath Path to the Component XML definition file
	 * @deprecated Use startComponent for all Component types instead.
	 * @return IFuture<IComponentIdentifier>	 
	 */
	protected IFuture<IComponentIdentifier> startBPMNAgent(final String name, final String modelPath) {
		return startComponent(name, modelPath);
	}
	
	public IFuture<IComponentIdentifier> startComponent(final String name, final String modelPath)
	{
		checkIfJadexIsRunning("startComponent()");
		return platformService.startComponent(name, modelPath);
	}
	
	public IFuture<IComponentIdentifier> startComponent(String name, String modelPath, CreationInfo creationInfo)
	{
		checkIfJadexIsRunning("startComponent()");
		return platformService.startComponent(name, modelPath, creationInfo);
	}

	public IFuture<IComponentIdentifier> startComponent(String name, Class< ? > clazz, CreationInfo creationInfo)
	{
		checkIfJadexIsRunning("startComponent()");
		return platformService.startComponent(name, clazz, creationInfo);
	}

	public IFuture<IComponentIdentifier> startComponent(String name, Class< ? > clazz)
	{
		checkIfJadexIsRunning("startComponent()");
		return platformService.startComponent(name, clazz);
	}

	public void registerEventReceiver(IEventReceiver<?> rec)
	{
		checkIfJadexIsRunning("registerEventReceiver");
		platformService.registerEventReceiver(rec);
	}

	public boolean unregisterEventReceiver(IEventReceiver<?> rec)
	{
		checkIfJadexIsRunning("unregisterEventReceiver");
		return platformService.unregisterEventReceiver(rec);
	}
	
	public boolean dispatchEvent(IJadexAndroidEvent event) throws WrongEventClassError
	{
		checkIfJadexIsRunning("dispatchEvent");
		return platformService.dispatchEvent(event);
	}
	
	/**
	 * Sends a Message to a Component on the Jadex Platform.
	 * 
	 * @param message
	 * @return Future<Void>
	 */
	protected IFuture<Void> sendMessage(final Map<String, Object> message, final IComponentIdentifier receiver)
	{
		checkIfJadexIsRunning("sendMessage");

		return getPlatformAccess().scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(IInternalAccess ia) {
				return ia.getFeature(IMessageFeature.class).sendMessage(receiver, message);
			}
		});

	}

	private void checkIfJadexIsRunning(String caller)
	{
		if (platformService == null || !platformService.isPlatformRunning(platformId))
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}
	
	/**
	 * @deprecated use getPlatformService().getSservice() instead.
	 * @return
	 */
	protected IFuture<IComponentManagementService> getCMS()
	{
		checkIfJadexIsRunning("getCMS");
		return platformService.getCMS(platformId);
	}

	public void onServiceConnected(ComponentName name, IBinder service)
	{
		platformService = (IJadexPlatformBinder) service;
		if (platformAutostart) {
			startPlatform();
		}
	}

	public void onServiceDisconnected(ComponentName name)
	{
		platformService = null;
	}

	/**
	 * Called right before the platform startup.
	 */
	protected void onPlatformStarting() {
		setProgressBarIndeterminateVisibility(true);
	}
	
	/**
	 * Called right after the platform is started.
	 * @param result The external access to the platform
	 */
	protected void onPlatformStarted(IExternalAccess result)
	{
		this.platformId = result.getId();
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				setProgressBarIndeterminateVisibility(false);
			}
		});
	}
	
	/**
	 * Starts the Jadex Platform.
	 * To set Parameters, use setPlatformKernels(), setPlatformOptions() or setPlatformName() before
	 * calling this Method.
	 * 
	 * Will be automatically called when setPlatformAutostart(true) was called in the Constructor.
	 * 
	 * The Lifecycle methods onPlatformStarting() and onPlatformStarted() will be executed
	 * during Startup.
	 */
	final protected void startPlatform()
	{
		onPlatformStarting();
		IFuture<IExternalAccess> platform = platformService.startJadexPlatform(platformConfiguration);
		
		platform.addResultListener(new DefaultResultListener<IExternalAccess>()
		{

			@Override
			public void resultAvailable(IExternalAccess result)
			{
				onPlatformStarted(result);
			}

		});
	}
	
	/**
	 * Stops all running jadex platforms.
	 * @deprecated use shutdownJadexPlatforms() instead.
	 */
	protected void stopPlatforms()
	{
		shutdownJadexPlatform();
	}

	public void shutdownJadexPlatform() {
		platformService.shutdownJadexPlatforms();
	}
	
	public void shutdownJadexPlatform(IComponentIdentifier platformID) {
		platformService.shutdownJadexPlatform(platformID);
	}

}
