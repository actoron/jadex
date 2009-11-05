package com.daimler.client.connector;

public class UserNotificationStateChangeEvent
{
	private UserNotification notification;
	
	public UserNotificationStateChangeEvent(UserNotification notification)
	{
		this.notification = notification;
	}
	
	public UserNotification getNotification()
	{
		return notification;
	}
}
