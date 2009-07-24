package com.daimler.client.connector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.daimler.client.gui.GuiClient;

public class ClientConnector
{
	private List requestQueue;
	
	private Set requestListeners;
	
	private static ClientConnector instance;
	
	public static synchronized ClientConnector getInstance()
	{
		if (instance == null)
		{
			instance = new ClientConnector();
			GuiClient client = new GuiClient();
			instance.addRequestListener(client);
		}
		return instance;
	}
	
	private ClientConnector()
	{
		requestQueue = new LinkedList();
		requestListeners = new HashSet();
	}
	
	
	public synchronized void dispatchRequest(ClientRequest request)
	{
		requestQueue.add(request);
		fireNewRequest(request);
	}
	
	public synchronized void finishRequest(ClientRequest request)
	{
		requestQueue.remove(request);
		fireFinishedRequest(request);
		request.getListener().resultAvailable(null);
	}
	
	public synchronized List getRequests()
	{
		return new ArrayList(requestQueue);
	}
	
	public synchronized void addRequestListener(IRequestListener listener)
	{
		requestListeners.add(listener);
	}
	
	public synchronized void removeRequestListener(IRequestListener listener)
	{
		requestListeners.remove(listener);
	}
	
	private synchronized void fireNewRequest(ClientRequest request)
	{
		for (Iterator it = requestListeners.iterator(); it.hasNext(); )
		{
			IRequestListener listener = (IRequestListener) it.next();
			listener.startedRequest(request);
		}
	}
	
	private synchronized void fireFinishedRequest(ClientRequest request)
	{
		synchronized (requestListeners)
		{
			for (Iterator it = requestListeners.iterator(); it.hasNext(); )
			{
				IRequestListener listener = (IRequestListener) it.next();
				listener.finishedRequest(request);
			}
		}
	}
}
