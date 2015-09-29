package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.commons.Logger;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.context.IJadexAndroidEvent;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.bridge.service.types.platform.IJadexPlatformBinder;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.ArrayList;
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
	
	private String[] platformKernels;
	private String platformOptions;
	private boolean platformAutostart;
	private String platformName;

	private IComponentIdentifier platformId;

	public JadexPlatformService()
	{
		jadexPlatformManager = JadexPlatformManager.getInstance();
		platformKernels = DEFAULT_KERNELS;
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
	 * Gets the Kernels.
	 * See {@link JadexPlatformManager} constants for available Kernels.
	 * @return String[] of kernels.
	 */
	protected String[] getPlatformKernels()
	{
		return platformKernels;
	}

	/**
	 * Sets the Kernels.
	 * See {@link JadexPlatformManager} constants for available Kernels.
	 * @param kernels
	 */
	protected void setPlatformKernels(String ... kernels) {
		this.platformKernels = kernels;
	}
	
	/**
	 * Returns the platform options of newly created platforms.
	 * @return String[] of options
	 */
	protected String getPlatformOptions()
	{
		return platformOptions;
	}

	/**
	 * Sets platform options.
	 * @param options
	 */
	protected void setPlatformOptions(String options) {
		this.platformOptions = options;
	}
	
	/**
	 * Returns the name which is used to create the next jadex platform.
	 * @return {@link String} name
	 */
	public String getPlatformName()
	{
		return platformName;
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
	 */
	protected void setPlatformName(String name) {
		this.platformName = name;
	}
	
	final protected IFuture<IExternalAccess> startPlatform()
	{
		Logger.i("Requested kernels: " + Arrays.toString(platformKernels));
		return startJadexPlatform(platformKernels, platformName, platformOptions);
	}
	
	public IFuture<IMessageService> getMS()
	{
		return getMS(platformId);
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

		getMS(platform).addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				ms.sendMessage(message, type, jadexPlatformManager.getExternalPlatformAccess(platform).getComponentIdentifier(), null, receiver, null)
						.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});

		return ret;
	}

}