package com.daimler.client.connector;

import jadex.bpmn.model.MParameter;
import jadex.bpmn.runtime.ITaskContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class ClientConnector
{
	private Set availableNotifications;
	
	private Set claimedNotifications;
	
	private Set notificationListeners;
	
	private static ClientConnector instance;
	
	public static synchronized ClientConnector getInstance()
	{
		if (instance == null)
		{
			instance = new ClientConnector();
			//new GuiClient();
			//new GuiClient();
		}
		return instance;
	}
	
	private ClientConnector()
	{
		availableNotifications = new HashSet();
		claimedNotifications = new HashSet();
		notificationListeners = new HashSet();
	}
	
	
	public synchronized void queueNotification(UserNotification notification)
	{
		availableNotifications.add(notification);
		fireNotificationAddedEvent(notification);
	}
	
	public synchronized void commitNotification(UserNotification notification, Map commitData)
	{
		if (claimedNotifications.contains(notification))
		{
			claimedNotifications.remove(notification);
			fireNotificationRemovedEvent(notification);
			if (notification.getType() == UserNotification.DATA_FETCH_NOTIFICATION_TYPE)
			{
				ITaskContext context = notification.getContext();
				for (Iterator it = commitData.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry parameter = (Map.Entry) it.next();
					context.setParameterValue((String) parameter.getKey(), parameter.getValue());
				}
			}
			notification.getListener().resultAvailable(null);
		}
		else
		{
			// TODO: Improve error handling
			throw new RuntimeException("Attempted to commit unclaimed Notification: " + notification.getType());
		}
	}
	
	public synchronized void claimNotification(UserNotification notification)
	{
		if (availableNotifications.contains(notification))
		{
			availableNotifications.remove(notification);
			claimedNotifications.add(notification);
			fireNotificationClaimedEvent(notification);
		}
		else
		{
			// TODO: Improve error handling
			throw new RuntimeException("Attempted to claim unavailable Notification: " + notification.getType());
		}
	}
	
	public synchronized void releaseNotification(UserNotification notification)
	{
		if (claimedNotifications.contains(notification))
		{
			claimedNotifications.remove(notification);
			availableNotifications.add(notification);
			fireNotificationReleasedEvent(notification);
		}
		else
		{
			// TODO: Improve error handling
			throw new RuntimeException("Attempted to release unclaimed Notification: " + notification.getType());
		}
	}
	
	public synchronized boolean isAvailable(UserNotification notification)
	{
		return availableNotifications.contains(notification);
	}
	
	public synchronized List getAvailableNotifications()
	{
		return new ArrayList(availableNotifications);
	}
	
	public synchronized void addNotificationStateListener(INotificationStateListener listener)
	{
		notificationListeners.add(listener);
	}
	
	public synchronized void removeNotificationStateListener(INotificationStateListener listener)
	{
		notificationListeners.remove(listener);
	}
	
	private synchronized void fireNotificationAddedEvent(UserNotification notification)
	{
		for (Iterator it = notificationListeners.iterator(); it.hasNext(); )
		{
			INotificationStateListener listener = (INotificationStateListener) it.next();
			listener.notificationAdded(new UserNotificationStateChangeEvent(notification));
		}
	}
	
	private synchronized void fireNotificationRemovedEvent(UserNotification notification)
	{
		synchronized (notificationListeners)
		{
			for (Iterator it = notificationListeners.iterator(); it.hasNext(); )
			{
				INotificationStateListener listener = (INotificationStateListener) it.next();
				listener.notificationRemoved(new UserNotificationStateChangeEvent(notification));
			}
		}
	}
	
	private synchronized void fireNotificationClaimedEvent(UserNotification notification)
	{
		for (Iterator it = notificationListeners.iterator(); it.hasNext(); )
		{
			INotificationStateListener listener = (INotificationStateListener) it.next();
			listener.notificationClaimed(new UserNotificationStateChangeEvent(notification));
		}
	}
	
	private synchronized void fireNotificationReleasedEvent(UserNotification notification)
	{
		synchronized (notificationListeners)
		{
			for (Iterator it = notificationListeners.iterator(); it.hasNext(); )
			{
				INotificationStateListener listener = (INotificationStateListener) it.next();
				listener.notificationReleased(new UserNotificationStateChangeEvent(notification));
			}
		}
	}
}
