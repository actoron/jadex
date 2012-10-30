package jadex.android.applications.chat;

import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.micro.annotation.Binding;

import java.util.Collection;

import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * Android service for running the Jadex platform.
 */
public class AndroidChatService extends jadex.android.service.JadexPlatformService
{
	// -------- attributes --------

	/** The platform. */
	protected IExternalAccess platform;

	private IChatGuiService chatgui;

	private ISubscriptionIntermediateFuture<ChatEvent> subscription;

	private ChatEventListener listener;

	public interface ChatEventListener
	{
		public void eventReceived(ChatEvent ce);
		public void chatConnected();
	}

	// -------- Android methods --------

	public AndroidChatService()
	{
		super();
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
		setPlatformOptions("-awareness true -niotcptransport false");
	}

	/**
	 * Called when an activity binds to the service.
	 */
	public IBinder onBind(Intent intent)
	{
		abstract class MyBinder extends Binder implements IAndroidChatService
		{
		}
		return new MyBinder()
		{

			@Override
			public IFuture<Void> sendMessage(String message)
			{
				return AndroidChatService.this.sendMessage(message);
			}

			@Override
			public void setChatEventListener(ChatEventListener l)
			{
				listener = l;
			}

			@Override
			public void removeMessageListener(ChatEventListener l)
			{
				if (listener == l)
				{
					listener = null;
				}
			}
		};
	}

	private IFuture<Void> subscribe()
	{
		return platform.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				final Future<Void> fut = new Future<Void>();
				ia.getServiceContainer().searchService(IChatGuiService.class, RequiredServiceInfo.SCOPE_PLATFORM)
						.addResultListener(new IResultListener<IChatGuiService>()
						{
							public void resultAvailable(IChatGuiService res)
							{
								chatgui = res;
								subscription = chatgui.subscribeToEvents();
								subscription.addResultListener(new IntermediateDefaultResultListener<ChatEvent>()
								{
									public void intermediateResultAvailable(ChatEvent ce)
									{
										if (ChatEvent.TYPE_MESSAGE.equals(ce.getType()))
										{
											if (listener != null)
											{
												listener.eventReceived(ce);
											}
										}
									}
								});
								fut.setResult(null);

							}

							@Override
							public void exceptionOccurred(Exception exception)
							{
								fut.setException(exception);
							}
						});
				return fut;
			}
		});
	}

	@Override
	protected void onPlatformStarted(IExternalAccess platform)
	{
		super.onPlatformStarted(platform);
		this.platform = platform;

		IFuture<Void> step = subscribe();
		step.addResultListener(new DefaultResultListener<Void>()
		{

			@Override
			public void resultAvailable(Void result)
			{
				if (listener != null)
				{
					listener.chatConnected();
				}
			}
		});

	}

	public IFuture<Void> sendMessage(final String message)
	{
		final Future<Void> fut = new Future<Void>();
		SServiceProvider.getService(platform.getServiceProvider(), IChatGuiService.class, Binding.SCOPE_PLATFORM).addResultListener(
				new DefaultResultListener<IChatGuiService>()
				{
					public void resultAvailable(IChatGuiService chat)
					{
						chat.message(message, new IComponentIdentifier[0], true).addResultListener(new IResultListener<Collection<IChatService>>()
						{
							public void resultAvailable(Collection<IChatService> result)
							{
								fut.setResult(null);
							}

							public void exceptionOccurred(Exception exception)
							{
								fut.setException(exception);
							}
						});
					}
				});
		return fut;
	}
}
