package com.daimler.client.connector;

import jadex.bpmn.runtime.ITaskContext;
import jadex.commons.concurrent.IResultListener;

public class ClientRequest
{
	private ITaskContext context;
	
	private IResultListener listener;
	
	public ClientRequest(ITaskContext context, IResultListener listener)
	{
		this.context = context;
		this.listener = listener;
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
