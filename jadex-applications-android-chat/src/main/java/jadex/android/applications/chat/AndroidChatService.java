package jadex.android.applications.chat;

import jadex.android.service.JadexPlatformManager;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateDefaultResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.micro.annotation.Binding;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

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

	private Set<ChatEventListener> listeners;

	private Handler uiHandler;

	public interface ChatEventListener
	{
		public void eventReceived(ChatEvent ce);

		public void chatConnected();
	}

	// -------- Android methods --------

	public AndroidChatService()
	{
		super();
		listeners = new HashSet<AndroidChatService.ChatEventListener>();
		setPlatformAutostart(true);
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
		setPlatformOptions("-awareness true -niotcptransport false");

		uiHandler = new Handler();
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
			public void addChatEventListener(ChatEventListener l)
			{
				listeners.add(l);
			}

			@Override
			public void removeMessageListener(ChatEventListener l)
			{
				listeners.remove(l);
			}

			@Override
			public IIntermediateFuture<ChatUser> getUsers()
			{
				return AndroidChatService.this.getUsers();
			}

			@Override
			public IFuture<Void> sendFile(String path, ChatUser user)
			{
				return AndroidChatService.this.sendFile(path, user);
			}

			@Override
			public Collection<TransferInfo> getTransfers()
			{
				return AndroidChatService.this.getTransfers();
			}

		};
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (subscription != null)
		{
			subscription.terminate();
		}
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
										publishEvent(ce);
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
				informChatConnected();
			}
		});

	}

	private void informChatConnected()
	{
		for (ChatEventListener l : listeners)
		{
			l.chatConnected();
		}
	}

	private void publishEvent(ChatEvent ce)
	{
		for (ChatEventListener l : listeners)
		{
			l.eventReceived(ce);
		}
	}

	// ----------- IAndroidChatService methods -----------

	private IFuture<Void> sendMessage(final String message)
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

	private IIntermediateFuture<ChatUser> getUsers()
	{
		final IntermediateFuture<ChatUser> fut = new IntermediateFuture<ChatUser>();
		List<ChatUser> result;

		chatgui.findUsers().addResultListener(new IntermediateDefaultResultListener<IChatService>()
		{

			private int waitCount = 0;
			private boolean finished = false;

			@Override
			public void intermediateResultAvailable(final IChatService chatService)
			{
				waitCount++;
				System.out.println("getting name for: " + chatService);
				chatService.getNickName().addResultListener(new DefaultResultListener<String>()
				{

					@Override
					public void resultAvailable(String nickName)
					{
						IServiceIdentifier sid = ((IService) chatService).getServiceIdentifier();
						ChatUser chatUser = new ChatUser(nickName, sid);
						fut.addIntermediateResult(chatUser);
						waitCount--;
						if (finished && (waitCount < 1))
						{
							finished();
						}
					}
				});
			}

			@Override
			public void finished()
			{
				if (waitCount > 0)
				{
					finished = true;
				} else
				{
					super.finished();
					fut.setFinished();
				}
			}

			@Override
			public void exceptionOccurred(final Exception exception)
			{
				super.exceptionOccurred(exception);
				uiHandler.post(new Runnable()
				{

					@Override
					public void run()
					{
						Toast.makeText(AndroidChatService.this, exception.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
				finished();
			}
		});

		return fut;
	}

	private Collection<TransferInfo> getTransfers()
	{
		ThreadSuspendable sus = new ThreadSuspendable();
		IIntermediateFuture<TransferInfo> fileTransfers = chatgui.getFileTransfers();
		Collection<TransferInfo> collection = fileTransfers.get(sus);
		return collection;
	}

	private IFuture<Void> sendFile(String path, ChatUser user)
	{
		return chatgui.sendFile(path, user.getSid().getProviderId());
	}

}
