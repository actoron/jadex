package com.daimler.client.connector;

import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

public class UserNotification
{
	public static final int TEXT_INFO_NOTIFICATION_TYPE = 0;
	public static final int DATA_FETCH_NOTIFICATION_TYPE = 1;
	
	private int type;
	
	private ITaskContext context;
	
	private IResultListener listener;
	
	public UserNotification(int type, ITaskContext context, IResultListener listener)
	{
		this.type = type;
		this.context = context;
		this.listener = listener;
	}
	
	public int getType()
	{
		return type;
	}
	
	public ITaskContext getContext()
	{
		return context;
	}
	
	public IResultListener getListener()
	{
		return listener;
	}
}
