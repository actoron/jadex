package jadex.android.applications.chat.service;

import jadex.android.applications.chat.ChatUser;
import jadex.android.applications.chat.NotificationHelper;
import jadex.android.applications.chat.model.UserModel;
import jadex.android.commons.JadexPlatformOptions;
import jadex.android.exception.JadexAndroidException;
import jadex.android.service.JadexPlatformService;
import jadex.base.IPlatformConfiguration;
import jadex.base.IExtendedPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.chat.ChatEvent;
import jadex.bridge.service.types.chat.IChatGuiService;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.chat.TransferInfo;
import jadex.commons.future.CounterResultListener;
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

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Android service for running the Jadex platform.
 */
public class AndroidChatService extends JadexPlatformService
{
	// -------- attributes --------

	/** The platform. */
	protected IExternalAccess platform;

	private IChatGuiService chatgui;

	private ISubscriptionIntermediateFuture<ChatEvent> subscription;

	private Set<ChatEventListener> listeners;

	private Handler uiHandler;

	private Map<String, TransferInfo> transfers;

	private Map<String, Integer> transferNotifications;

	private Queue<ChatEvent> newMessages;

	private NotificationHelper notificationHelper;
	
	private UserModel userModel;

	private Timer timer;

	public interface ChatEventListener
	{
		public boolean eventReceived(ChatEvent ce);

		public void chatConnected();
	}

	// -------- Android methods --------

	public AndroidChatService()
	{
		super();
		listeners = new HashSet<AndroidChatService.ChatEventListener>();
		transfers = new HashMap<String, TransferInfo>();
		userModel = new UserModel();
		
		newMessages = new LinkedList<ChatEvent>();

		setPlatformAutostart(true);
		setPlatformKernels(IPlatformConfiguration.KERNEL_MICRO);
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimalComm();
		config.setLogging(false);
		config.setNetworkNames("jadexnetwork");
		config.setNetworkSecrets("laxlax");
		IExtendedPlatformConfiguration rootConfig = config.getExtendedPlatformConfiguration();
		rootConfig.setChat(true);
		rootConfig.setAwaMechanisms(
			IPlatformConfiguration.AWAMECHANISM_BROADCAST,
			IPlatformConfiguration.AWAMECHANISM_MULTICAST,
//			IPlatformConfiguration.AWAMECHANISM_MESSAGE,
//			IPlatformConfiguration.AWAMECHANISM_RELAY,
			IPlatformConfiguration.AWAMECHANISM_LOCAL
//			IPlatformConfiguration.AWAMECHANISM.bluetooth)
		);

//		setPlatformOptions(
//				"-awareness true " +
//						"-chat true " +
//						"-log false " +
//						"-niotcptransport false " +
//						"-networkname \"jadexnetwork\" " +
//						"-networkpass \"laxlax\" " +
//						"-bluetoothtransport true " +
//						"-awamechanisms \"Broadcast, Multicast, Message, Relay, Local, Bluetooth\"");

		setPlatformConfiguration(config);
		uiHandler = new Handler();
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		timer = new Timer();
		notificationHelper = new NotificationHelper(getBaseContext());
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
				if (isConnected()) {
					l.chatConnected();
				}
				for (ChatEvent ce : newMessages.toArray(new ChatEvent[newMessages.size()]))
				{
					boolean eventReceived = l.eventReceived(ce);
					if (eventReceived)
					{
						newMessages.remove(ce);
					}
				}
			}

			@Override
			public void removeMessageListener(ChatEventListener l)
			{
				listeners.remove(l);
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

			@Override
			public IFuture<Void> acceptFileTransfer(TransferInfo ti)
			{
				return AndroidChatService.this.acceptFileTransfer(ti);
			}

			@Override
			public IFuture<Void> rejectFileTransfer(TransferInfo ti)
			{
				return AndroidChatService.this.rejectFileTransfer(ti);
			}

			@Override
			public IFuture<Void> cancelFileTransfer(TransferInfo ti)
			{
				return AndroidChatService.this.cancelFileTransfer(ti);
			}

			@Override
			public boolean isConnected()
			{
				return (AndroidChatService.this.platform != null);
			}
			
			@Override
			public IIntermediateFuture<IChatService> setStatus(String status, byte[] image, IComponentIdentifier[] receivers) {
				if (chatgui != null) {
					return chatgui.status(status, image, receivers);
				} else {
					IntermediateFuture<IChatService> intermediateFuture = new IntermediateFuture<IChatService>();
					intermediateFuture.setException(new JadexAndroidException("Chat is offline, cannot set status."));
					return intermediateFuture;
				}
			}
			
			@Override
			public void shutdown() {
				AndroidChatService.this.stopPlatforms();
				stopSelf();
			}

			@Override
			public IFuture<String> getNickname() {
				return chatgui.getNickName();
			}

			@Override
			public void setNickname(String name) {
				chatgui.setNickName(name);
			}

			@Override
			public UserModel getUserModel() {
				return userModel;
			}
		};
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		timer.cancel();
		timer.purge();
		if (subscription != null)
		{
			subscription.terminate();
		}
		notificationHelper.discardAll();
	}

	private IFuture<Void> subscribe()
	{
		final Future<Void> fut = new Future<Void>();
		platform.searchService( new ServiceQuery<>( IChatGuiService.class, RequiredServiceInfo.SCOPE_PLATFORM))
				.addResultListener(new IResultListener<IChatGuiService>()
				{
					public void resultAvailable(IChatGuiService service)
					{
						chatgui = service;
						subscription = chatgui.subscribeToEvents();
						subscription.addResultListener(new IntermediateDefaultResultListener<ChatEvent>()
						{
							public void intermediateResultAvailable(ChatEvent ce)
							{
								informChatEvent(ce);
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
				timer.schedule(new TimerTask() {
					
					@Override
					public void run() {
						refreshUsers();		
					}
				}, 20000);
				
				informChatConnected();
			}
		});

	}

	protected void refreshUsers() {
		System.out.println("Refreshing model");
		getUsers().addResultListener(new IntermediateDefaultResultListener<ChatUser>() {
			@Override
			public void intermediateResultAvailable(ChatUser result) {
				userModel.refreshUser(result);
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

	private void informChatEvent(ChatEvent ce)
	{
		boolean eventProcessed = false;
		for (ChatEventListener l : listeners)
		{
			System.out.println("informing listener " + l + " about " + ce);
			eventProcessed = l.eventReceived(ce) || eventProcessed;
		}
		processEvent(ce, eventProcessed);
	}

	private void processEvent(ChatEvent ce, boolean alreadyProcessed)
	{
		if (ce.getType().equals(ChatEvent.TYPE_FILE))
		{
			TransferInfo ti = (TransferInfo) ce.getValue();
			notificationHelper.createOrUpdateFileNotification(ti, ce.getNick());

		} else if (ce.getType().equals(ChatEvent.TYPE_MESSAGE) && !alreadyProcessed)
		{
			newMessages.offer(ce);
			String m = (String) ce.getValue();
			notificationHelper.showMessageNotification(m, ce.getNick(), newMessages.size());

		} else if (ce.getType().equals(ChatEvent.TYPE_STATECHANGE)) 
		{
			userModel.refreshUser(ce.getComponentIdentifier(), ce);
			
		}
	}


	// ----------- IAndroidChatService methods -----------

	private IFuture<Void> sendMessage(final String message)
	{
		final Future<Void> fut = new Future<Void>();
		platform.searchService( new ServiceQuery<>( IChatGuiService.class, Binding.SCOPE_PLATFORM)).addResultListener(
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
				final ChatUser chatUser = new ChatUser("", null);
				
				final CounterResultListener<Void> resLis = new CounterResultListener<Void>(2, new DefaultResultListener<Void>() {

					@Override
					public void resultAvailable(Void result) {
						waitCount--;
						fut.addIntermediateResult(chatUser);									
						if (finished && (waitCount < 1))
						{
							finished();
						}
					}
				});
				
				chatService.getNickName().addResultListener(new DefaultResultListener<String>()
				{
					@Override
					public void resultAvailable(String nickName)
					{
						IServiceIdentifier sid = ((IService) chatService).getServiceIdentifier();
						chatUser.setNickName(nickName);
						chatUser.setCid(sid.getProviderId());
						resLis.resultAvailable(null);
					}
				});
				
				chatService.getStatus().addResultListener(new DefaultResultListener<String>() 
				{
					@Override
					public void resultAvailable(String result) {
						chatUser.setStatus(result);
						resLis.resultAvailable(null);
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
		IIntermediateFuture<TransferInfo> fileTransfers = chatgui.getFileTransfers();
		Collection<TransferInfo> collection = fileTransfers.get();
		return collection;
	}

	private IFuture<Void> sendFile(String path, ChatUser user)
	{
		return chatgui.sendFile(path, user.getCid());
	}

	private IFuture<Void> acceptFileTransfer(TransferInfo ti)
	{
		setDownloadPath(ti);
		return chatgui.acceptFile(ti.getId(), ti.getFilePath());
	}

	private IFuture<Void> rejectFileTransfer(TransferInfo ti)
	{
		return chatgui.rejectFile(ti.getId());
	}

	private IFuture<Void> cancelFileTransfer(TransferInfo ti)
	{
		return chatgui.cancelTransfer(ti.getId());
	}
	
	// ----- HELPER --------

	private static void setDownloadPath(TransferInfo ti)
	{
		File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

		if (!downloadDir.exists())
		{
			downloadDir.mkdir();
		}

		String fName = new File(downloadDir, ti.getFileName()).getAbsolutePath();
		if (new File(fName).exists())
		{

			Pattern p = Pattern.compile("(.*?)(\\(\\d+\\))?(\\.\\w*)?");
			do
			{
				Matcher m = p.matcher(fName);
				if (m.matches())
				{// group 1 is the prefix, group 2 is the number, group 3 is the
					// suffix
					fName = m.group(1) + (m.group(2) == null ? "(1)" : "(" + (Integer.parseInt(m.group(2).replaceAll("\\D", "")) + 1) + ")")
							+ (m.group(3) == null ? "" : m.group(3));
				}
			} while (new File(fName).exists());// repeat until a new filename is
												// generated
		}

		ti.setFilePath(fName);
	}

}
