package jadex.android.service;

import jadex.android.AndroidContextManager;
import jadex.android.IEventReceiver;
import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.message.IMessageService;
import jadex.bridge.service.types.message.MessageType;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.transformation.annotations.Classname;

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

	protected JadexPlatformManager jadexPlatformManager;

	@Override
	public IBinder onBind(Intent intent)
	{
		return new JadexPlatformBinder(jadexPlatformManager)
		{

			@Override
			public IFuture<IComponentIdentifier> startMicroAgent(IComponentIdentifier platformId, String name, Class<?> clazz)
			{
				return JadexPlatformService.this.startMicroAgent(platformId, name, clazz);
			}

			@Override
			public IFuture<IComponentIdentifier> startBPMNAgent(final IComponentIdentifier platformId, String name, String modelPath)
			{
				return JadexPlatformService.this.startBPMNAgent(platformId, name, modelPath);
			}

		};
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		jadexPlatformManager = new JadexPlatformManager();
		AndroidContextManager.getInstance().setAndroidContext(this);
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

		getMS(platform).addResultListener(new DefaultResultListener<IMessageService>()
		{
			public void resultAvailable(IMessageService ms)
			{
				ms.sendMessage(message, type, jadexPlatformManager.getExternalPlatformAccess(platform).getComponentIdentifier(), null, receiver, null)
						.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});

		return ret;
	}

	private void checkIfPlatformIsRunning(final IComponentIdentifier platformId, String caller)
	{
		if (!jadexPlatformManager.isPlatformRunning(platformId))
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}

	private IFuture<IMessageService> getMS(final IComponentIdentifier platformId)
	{
		return jadexPlatformManager.getExternalPlatformAccess(platformId).scheduleStep(new IComponentStep<IMessageService>()
		{
			@Classname("create-component")
			public IFuture<IMessageService> execute(IInternalAccess ia)
			{
				Future<IMessageService> ret = new Future<IMessageService>();
				SServiceProvider.getService(ia.getServiceContainer(), IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(
						ia.createResultListener(new DelegationResultListener<IMessageService>(ret)));

				return ret;
			}
		});
	}
}
