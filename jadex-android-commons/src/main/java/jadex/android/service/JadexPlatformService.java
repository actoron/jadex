package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Android Service to start/stop Jadex Platforms. Platforms are terminated on
 * destroy.
 */
public class JadexPlatformService extends Service
{

	private JadexPlatformManager jadexPlatformManager;
	
	private String[] platformKernels;
	private String platformOptions;
	private boolean platformAutostart;
	private String platformName;

	private IComponentIdentifier platformId;
	
	public JadexPlatformService()
	{
		jadexPlatformManager = JadexPlatformManager.getInstance();
		platformKernels = JadexPlatformManager.DEFAULT_KERNELS;
	}

	@Override
	public IBinder onBind(Intent intent)
	{
		return new JadexPlatformBinder(jadexPlatformManager)
		{

			public IFuture<IComponentIdentifier> startMicroAgent(IComponentIdentifier platformId, String name, Class<?> clazz)
			{
				return JadexPlatformService.this.startMicroAgent(platformId, name, clazz);
			}

			public IFuture<IComponentIdentifier> startBPMNAgent(final IComponentIdentifier platformId, String name, String modelPath)
			{
				return JadexPlatformService.this.startBPMNAgent(platformId, name, modelPath);
			}
			
			public IFuture<IComponentIdentifier> startBDIAgent(final IComponentIdentifier platformId, String name, String modelPath)
			{
				return JadexPlatformService.this.startBDIAgent(platformId, name, modelPath);
			}

			public IFuture<IExternalAccess> startJadexPlatform()
			{
				return startJadexPlatform(jadexPlatformManager.DEFAULT_KERNELS);
			}

			public IFuture<IExternalAccess> startJadexPlatform(String[] kernels)
			{
				return startJadexPlatform(kernels, jadexPlatformManager.getRandomPlatformID());
			}

			public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId)
			{
				return startJadexPlatform(kernels, platformId, "");
			}

			public IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId, String options)
			{
				return JadexPlatformService.this.startJadexPlatform(kernels, platformId, options);
			}

		};
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		AndroidContextManager.getInstance().setAndroidContext(this);
		if (platformAutostart) {
			startPlatform();
		}
		// jadexAndroidContext.addContextChangeListener(this);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		jadexPlatformManager.shutdownJadexPlatforms();
		AndroidContextManager.getInstance().setAndroidContext(null);
		// jadexAndroidContext.removeContextChangeListener(this);
	}
	
	/**
	 * Sets the autostart parameter for this jadex platform.
	 * If true, the platform will be started during onCreate.
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
	 * See {@link JadexPlatformManager} Constants for available Kernels.
	 * @param kernels
	 */
	protected void setPlatformKernels(String ... kernels) {
		this.platformKernels = kernels;
	}
	
	/**
	 * Sets platform options.
	 * @param options
	 */
	protected void setPlatformOptions(String options) {
		this.platformOptions = options;
	}
	
	/**
	 * Sets the name of the platform that is started by this activity.
	 * @param name
	 */
	protected void setPlatformName(String name) {
		this.platformName = name;
	}
	
	final protected IFuture<IExternalAccess> startPlatform()
	{
		return startJadexPlatform(platformKernels, platformName, platformOptions);
	}
	
	protected void stopPlatforms()
	{
		jadexPlatformManager.shutdownJadexPlatforms();
	}
	
	protected IExternalAccess getPlatformAccess(IComponentIdentifier platformId) {
		checkIfPlatformIsRunning(platformId, "getPlatformAccess");
		return jadexPlatformManager.getExternalPlatformAccess(platformId);
	}
	
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
	 */
	public IFuture<IComponentIdentifier> startMicroAgent(final IComponentIdentifier platformId, final String name, final Class<?> clazz)
	{
		checkIfPlatformIsRunning(platformId, "startMicroAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		jadexPlatformManager.getCMS(platformId).addResultListener(new DefaultResultListener<IComponentManagementService>()
		{

			public void resultAvailable(IComponentManagementService cms)
			{
				HashMap<String, Object> args = new HashMap<String, Object>();

				cms.createComponent(name, clazz.getName().replaceAll("\\.", "/") + ".class", new CreationInfo(args), null).addResultListener(
						new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});

		return ret;
	}
	
	/**
	 * Start a new BDI agent on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created agent
	 * @param modelPath
	 *            Path to the bpmn model file of the new agent
	 * @return ComponendIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startBDIAgent(final IComponentIdentifier platformId, final String name, final String modelPath) {
		return startBPMNAgent(platformId, name, modelPath);
	}

	/**
	 * Start a new BPMN agent on a given platform.
	 * 
	 * @param platformId
	 *            Identifier of the jadex platform
	 * @param name
	 *            name of the newly created agent
	 * @param modelPath
	 *            Path to the bpmn model file of the new agent
	 * @return ComponendIdentifier of the created agent.
	 */
	public IFuture<IComponentIdentifier> startBPMNAgent(final IComponentIdentifier platformId, final String name, final String modelPath)
	{
		checkIfPlatformIsRunning(platformId, "startBPMNAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		jadexPlatformManager.getCMS(platformId).addResultListener(new DefaultResultListener<IComponentManagementService>()
		{

			public void resultAvailable(IComponentManagementService cms)
			{
				HashMap<String, Object> args = new HashMap<String, Object>();

				args.put("androidContext", JadexPlatformService.this);
				cms.createComponent(name, modelPath, new CreationInfo(args), null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});

		return ret;
	}

	public void registerEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		AndroidContextManager.getInstance().registerEventListener(eventName, rec);
	}

	public void unregisterEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		AndroidContextManager.getInstance().unregisterEventListener(eventName, rec);
	}

	/**
	 * Called right before the platform startup.
	 */
	protected void onPlatformStarting()
	{
	}

	/**
	 * Called right after the platform is started.
	 * 
	 * @param result
	 *            The external access to the platform
	 */
	protected void onPlatformStarted(IExternalAccess platform)
	{
		this.platformId = platform.getComponentIdentifier();
	}

	/**
	 * Sends a FIPA Message to the specified receiver. The Sender is
	 * automatically set to the Platform.
	 * 
	 * @param message
	 * @param receiver
	 * @return Future<Void>
	 */
	protected Future<Void> sendMessage(final Map<String, Object> message, IComponentIdentifier receiver)
	{
		message.put(SFipa.FIPA_MESSAGE_TYPE.getReceiverIdentifier(), receiver);
		IComponentIdentifier cid = jadexPlatformManager.getExternalPlatformAccess(receiver.getRoot()).getComponentIdentifier();
		message.put(SFipa.FIPA_MESSAGE_TYPE.getSenderIdentifier(), cid);
		return sendMessage(message, SFipa.FIPA_MESSAGE_TYPE, receiver);
	}

	/**
	 * Sends a Message to a Component on the Jadex Platform.
	 * 
	 * @param message
	 * @param type
	 * @return Future<Void>
	 */
	protected Future<Void> sendMessage(final Map<String, Object> message, final MessageType type, final IComponentIdentifier receiver)
	{
		final IComponentIdentifier platform = receiver.getRoot();
		checkIfPlatformIsRunning(platform, "sendMessage");

		final Future<Void> ret = new Future<Void>();

		jadexPlatformManager.getMS(platform).addResultListener(new DefaultResultListener<IMessageService>()
		{
			public void resultAvailable(IMessageService ms)
			{
				ms.sendMessage(message, type, jadexPlatformManager.getExternalPlatformAccess(platform).getComponentIdentifier(), null, receiver, null)
						.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});

		return ret;
	}
	
	//---------------- helper ----------------
	
	final private IFuture<IExternalAccess> startJadexPlatform(String[] kernels, String platformId, String options)
	{
		onPlatformStarting();
		IFuture<IExternalAccess> fut = jadexPlatformManager.startJadexPlatform(kernels, platformId, options);
		fut.addResultListener(new DefaultResultListener<IExternalAccess>()
		{
			@Override
			public void resultAvailable(IExternalAccess result)
			{
				JadexPlatformService.this.onPlatformStarted(result);
			}
		});
		return fut;
	}
	
	private void checkIfPlatformIsRunning(final IComponentIdentifier platformId, String caller)
	{
		if (!jadexPlatformManager.isPlatformRunning(platformId))
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}

}
