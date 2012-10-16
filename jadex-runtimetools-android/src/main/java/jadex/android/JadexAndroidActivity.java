package jadex.android;

import jadex.android.exception.JadexAndroidPlatformNotStartedError;
import jadex.android.service.IJadexPlatformBinder;
import jadex.android.service.JadexPlatformService;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
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

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

/**
 * This is an Android Activity Class which provides needed Functionality and
 * comfort Features for Jadex Android Activities. It MUST be extended by every
 * Jadex-Android Activity.
 * 
 * @author Julian Kalinowski
 */
public class JadexAndroidActivity extends Activity implements ServiceConnection
{

	private Intent serviceIntent;
	private IJadexPlatformBinder platformService;
	protected IComponentIdentifier platformId;

	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		serviceIntent = new Intent(this, JadexPlatformService.class);
		bindService(serviceIntent, this, Service.BIND_AUTO_CREATE);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		unbindService(this);
	}

	// /**
	// * Returns the External Access Object of the Jadex Platform.
	// *
	// * @return {@link IExternalAccess}
	// */
	// public IExternalAccess getExternalPlatformAccess() throws
	// JadexAndroidPlatformNotStartedError
	// {
	// checkIfJadexIsRunning("getExternalPlatformAccess()");
	// return jadexAndroidContext.getExternalPlattformAccess();
	// }

	protected boolean isJadexPlatformRunning()
	{
		if (platformService != null) {
			return platformService.isPlatformRunning(platformId);
		} else {
			return false;
		}
	}

	protected boolean isJadexPlatformRunning(IComponentIdentifier platformId)
	{
		if (platformService != null) {
			return platformService.isPlatformRunning(platformId);
		} else {
			return false;
		}
	}

	protected IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz)
	{
		checkIfJadexIsRunning("startMicroAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		platformService.getCMS(platformId).addResultListener(new DefaultResultListener<IComponentManagementService>()
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

	protected IFuture<IComponentIdentifier> startBPMNAgent(final String name, final String modelPath)
	{
		checkIfJadexIsRunning("startBPMNAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		platformService.getCMS(platformId).addResultListener(new DefaultResultListener<IComponentManagementService>()
		{

			public void resultAvailable(IComponentManagementService cms)
			{
				HashMap<String, Object> args = new HashMap<String, Object>();

				args.put("androidContext", JadexAndroidActivity.this);
				cms.createComponent(name, modelPath, new CreationInfo(args), null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});

		return ret;
	}

	protected void registerEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		platformService.registerEventListener(eventName, rec);
	}

	protected void unregisterEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		platformService.unregisterEventListener(eventName, rec);
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
		IComponentIdentifier cid = platformId;
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
		checkIfJadexIsRunning("sendMessage");

		final Future<Void> ret = new Future<Void>();

		getMS().addResultListener(new DefaultResultListener<IMessageService>()
		{
			public void resultAvailable(IMessageService ms)
			{
				ms.sendMessage(message, type, platformId, null, receiver, null).addResultListener(new DelegationResultListener<Void>(ret));
			}
		});

		return ret;
	}

	private void checkIfJadexIsRunning(String caller)
	{
		if (!platformService.isPlatformRunning(platformId))
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}

	private IFuture<IMessageService> getMS()
	{
		return platformService.getExternalPlatformAccess(platformId).scheduleStep(new IComponentStep<IMessageService>()
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

	public void onServiceConnected(ComponentName name, IBinder service)
	{
		platformService = (IJadexPlatformBinder) service;
		onPlatformStarting();
		IFuture<IExternalAccess> platform = startPlatform(platformService);
		platform.addResultListener(new DefaultResultListener<IExternalAccess>()
		{

			@Override
			public void resultAvailable(IExternalAccess result)
			{
				onPlatformStarted(result);
			}

		});
	}

	public void onServiceDisconnected(ComponentName name)
	{
		platformService = null;
	}

	
	/**
	 * Terminates a given Jadex platform.
	 * @param platformID Identifier of the platform to terminate
	 */
	public void shutdownJadexPlatform(IComponentIdentifier platformID) {
		platformService.shutdownJadexPlatform(platformID);
	}
	

	/**
	 * Starts the jadex Platform.
	 * Override to use your own options (use platformService.startJadexPlatform()).
	 * @param platformService
	 * @return external access of the running platform
	 */
	protected IFuture<IExternalAccess> startPlatform(IJadexPlatformBinder platformService)
	{
		return platformService.startJadexPlatform();
	}
	
	/**
	 * Called right before the platform startup.
	 */
	protected void onPlatformStarting() {}
	
	/**
	 * Called right after the platform is started.
	 * @param result The external access to the platform
	 */
	protected void onPlatformStarted(IExternalAccess result)
	{
		this.platformId = result.getComponentIdentifier();
	}


}
