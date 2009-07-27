package com.daimler.client.connector;

public interface INotificationStateListener
{
	public void notificationAdded(UserNotificationStateChangeEvent event);
	
	public void notificationRemoved(UserNotificationStateChangeEvent event);
	
	public void notificationClaimed(UserNotificationStateChangeEvent event);
	
	public void notificationReleased(UserNotificationStateChangeEvent event);
}
