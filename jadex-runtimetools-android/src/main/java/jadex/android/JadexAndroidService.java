package jadex.android;

import jadex.android.JadexAndroidContext.AndroidContextChangeListener;
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
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class JadexAndroidService extends Service implements AndroidContextChangeListener
{
	
	private JadexAndroidContext jadexAndroidContext;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		jadexAndroidContext = JadexAndroidContext.getInstance();
		jadexAndroidContext.setAndroidContext(this);
		jadexAndroidContext.addContextChangeListener(this);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		jadexAndroidContext.removeContextChangeListener(this);
	}
	
	protected boolean isJadexPlatformRunning()
	{
		return jadexAndroidContext.isPlatformRunning();
	}
	
	protected boolean isJadexPlatformRunning(IComponentIdentifier platformId)
	{
		return jadexAndroidContext.isPlatformRunning(platformId);
	}
	
	protected JadexAndroidContext getJadexContext() {
		return jadexAndroidContext;
	}

	protected IFuture<IComponentIdentifier> startMicroAgent(final String name, final Class<?> clazz)
	{
		checkIfJadexIsRunning("startMicroAgent()");
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();
		jadexAndroidContext.getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
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
		jadexAndroidContext.getCMS().addResultListener(new DefaultResultListener<IComponentManagementService>()
		{

			public void resultAvailable(IComponentManagementService cms)
			{
				HashMap<String, Object> args = new HashMap<String, Object>();

				args.put("androidContext", JadexAndroidService.this);
				cms.createComponent(name, modelPath, new CreationInfo(args), null).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
			}
		});

		return ret;
	}

	protected void registerEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		jadexAndroidContext.registerEventListener(eventName, rec);
	}

	protected void unregisterEventReceiver(String eventName, IEventReceiver<?> rec)
	{
		jadexAndroidContext.unregisterEventListener(eventName, rec);
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
		IComponentIdentifier cid = jadexAndroidContext.getExternalPlatformAccess().getComponentIdentifier();
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
				ms.sendMessage(message, type, jadexAndroidContext.getExternalPlatformAccess().getComponentIdentifier(), null, receiver, null).addResultListener(
						new DelegationResultListener<Void>(ret));
			}
		});

		return ret;
	}

	private void checkIfJadexIsRunning(String caller)
	{
		if (!jadexAndroidContext.isPlatformRunning())
		{
			throw new JadexAndroidPlatformNotStartedError(caller);
		}
	}

	private IFuture<IMessageService> getMS()
	{
		return jadexAndroidContext.getExternalPlatformAccess().scheduleStep(new IComponentStep<IMessageService>()
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

	public void onContextDestroy(Context ctx)
	{
		// if context is destroyed
		System.out.println("service sets context");
		jadexAndroidContext.setAndroidContext(this);
	}

	public void onContextCreate(Context ctx)
	{
		// dont care
	}

}
