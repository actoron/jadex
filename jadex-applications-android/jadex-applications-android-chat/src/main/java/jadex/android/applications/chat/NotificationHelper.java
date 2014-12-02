package jadex.android.applications.chat;

import jadex.android.applications.chat.filetransfer.TransferActivity;
import jadex.android.applications.chat.fragments.ChatFragment;
import jadex.android.commons.Logger;
import jadex.bridge.service.types.chat.TransferInfo;

import java.util.HashMap;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.TaskStackBuilder;

public class NotificationHelper
{

	private NotificationManager notificationManager;
	private Builder notificationBuilder;
	private Context context;
	private Map<String, Integer> transferNotifications;

	private Integer nextNotificationId = 0;
	
	private final int messageIcon = android.R.drawable.stat_notify_chat;
	private final int downloadIcon = android.R.drawable.stat_sys_download;
	private final int uploadIcon = android.R.drawable.stat_sys_upload;
	private final int downloadFinishedIcon =android.R.drawable.stat_sys_download_done;
	private final int uploadFinishedIcon =android.R.drawable.stat_sys_upload_done;
	private final int downloadWaitingIcon = android.R.drawable.stat_notify_sdcard;

	public NotificationHelper(Context ctx)
	{
		this.context = ctx;
		transferNotifications = new HashMap<String, Integer>();

		notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationBuilder = new NotificationCompat.Builder(context);

		notificationBuilder.setAutoCancel(true);

		showServiceNotification();
	}

	public void createOrUpdateFileNotification(TransferInfo ti, String peerNick)
	{
		Logger.d("trying to create notification with context: " + context);
		String id = ti.getId();
		if (ti.getState().equals(TransferInfo.STATE_TRANSFERRING))
		{
			notificationBuilder.setOngoing(true);
			notificationBuilder.setTicker("Transferring file ...");
			notificationBuilder.setContentText(ti.getFileName()).setContentTitle("Transferring file...");
			if (ti.isDownload())
			{
				notificationBuilder.setSmallIcon(downloadIcon);
			} else
			{
				notificationBuilder.setSmallIcon(uploadIcon);
			}
			notificationBuilder.setContentIntent(createTransferActivityPendingIntent(null,null));
		} else if (ti.getState().equals(TransferInfo.STATE_WAITING))
		{
			notificationBuilder.setSmallIcon(downloadWaitingIcon);
			notificationBuilder.setOngoing(false);
			notificationBuilder.setAutoCancel(true);
			notificationBuilder.setContentText(ti.getFileName()).setContentTitle("File offer");
			notificationBuilder.setTicker("File offer");
			notificationBuilder.setContentIntent(createTransferActivityPendingIntent(ti, peerNick));
		} else if (ti.getState().equals(TransferInfo.STATE_COMPLETED))
		{
			notificationBuilder.setOngoing(false);
			notificationBuilder.setAutoCancel(true);
			notificationBuilder.setContentText(ti.getFileName()).setContentTitle("Transfer complete!");
			notificationBuilder.setTicker("Transfer complete!");
			notificationBuilder.setContentIntent(createTransferActivityPendingIntent(null,null));
			if (ti.isDownload())
			{
				notificationBuilder.setSmallIcon(downloadFinishedIcon);
			} else
			{
				notificationBuilder.setSmallIcon(uploadFinishedIcon);
			}
		} else
		{
			notificationBuilder.setOngoing(false);
			notificationBuilder.setAutoCancel(true);
			notificationBuilder.setContentText(ti.getFileName()).setContentTitle("Transfer aborted");
			notificationBuilder.setTicker("Transfer aborted");
			notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
		}
		

		// create or update notification
		Integer notId = transferNotifications.get(id);
		if (notId != null)
		{
			// update notification
		} else
		{
			// new notification
			notId = ++nextNotificationId;
		}

		notificationBuilder.setNumber(0);

		Notification notification = notificationBuilder.getNotification();
		notificationManager.notify(notId, notification);
		transferNotifications.put(id, notId);
	}

	public void showMessageNotification(String message, String sender, int count)
	{
		notificationBuilder.setSmallIcon(messageIcon);
		notificationBuilder.setContentTitle("Message from: " + sender);
		notificationBuilder.setContentText(message);
		notificationBuilder.setContentIntent(createChatActivityPendingIntent());
		notificationBuilder.setOngoing(false);
		notificationBuilder.setAutoCancel(true);

		notificationBuilder.setNumber(count);
		System.out.println("New Message count: " + count);

		Notification notification = notificationBuilder.getNotification();
		notificationManager.notify("message", 0, notification);
	}

	public void showServiceNotification()
	{
		notificationBuilder.setAutoCancel(false);
		notificationBuilder.setSmallIcon(android.R.drawable.stat_notify_chat);
		notificationBuilder.setContentTitle("Jadex Chat");
		notificationBuilder.setContentText("Tap to return to chat");
		notificationBuilder.setContentIntent(createChatActivityPendingIntent());

		notificationBuilder.setNumber(1);
		notificationBuilder.setOngoing(true);
		Notification persistentNotification = notificationBuilder.getNotification();
		notificationManager.notify(0, persistentNotification);
		System.out.println("Creating service notification");
	}

	private PendingIntent createChatActivityPendingIntent()
	{
		Intent notifyIntent = new Intent(context, ChatFragment.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		return pendingIntent;
	}

	private PendingIntent createTransferActivityPendingIntent(TransferInfo ti, String nick)
	{
		// TODO convert transferactivity to clientfragment
		Intent resultIntent = new Intent(context, TransferActivity.class);
		if (ti != null && nick != null) {
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_TRANSFERINFO, ti);
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_OTHERNICK, nick);
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_METHOD, TransferActivity.EXTRA_METHOD_CREATE);
		} else {
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_TRANSFERINFO, "");
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_OTHERNICK, "");
			resultIntent.putExtra(TransferActivity.EXTRA_KEY_METHOD, "");
		}
		
		TaskStackBuilder stackBuilder = TaskStackBuilder.from(context);
//		stackBuilder.addParentStack(TransferActivity.class); // results in error because TransferActivity is not declared
		stackBuilder.addNextIntent(resultIntent);
		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);
	}

	public void discardAll()
	{
		notificationManager.cancel(0);
	}
}
