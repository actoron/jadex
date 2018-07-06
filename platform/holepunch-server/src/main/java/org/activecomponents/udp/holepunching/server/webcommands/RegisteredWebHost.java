package org.activecomponents.udp.holepunching.server.webcommands;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.activecomponents.udp.holepunching.server.IRegisteredHost;

public class RegisteredWebHost implements IRegisteredHost
{
	protected BlockingQueue<String> msgqueue;
	
	/** Timestamp of last activity */
	protected volatile long lastactivity;
	
	public RegisteredWebHost()
	{
		msgqueue = new LinkedBlockingQueue<String>();
		lastactivity = System.currentTimeMillis();
	}
	
	/**
	 *  Writes a message to the connected host.
	 *  
	 *  @param msg The message.
	 */
	public void writeMsg(String msg)
	{
		msgqueue.offer(msg);
	}
	
	public String readMsg(long timeout)
	{
		String ret = null;
		try
		{
			ret = msgqueue.poll(timeout, TimeUnit.MILLISECONDS);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
		return ret;
	}

	/**
	 *  Gets the last activity.
	 *  @return The last activity.
	 */
	public long getLastActivity()
	{
		return lastactivity;
	}

	/**
	 *  Sets the last activity.
	 *  @param lastactivity The last activity to set
	 */
	public void setLastActivity(long lastactivity)
	{
		this.lastactivity = lastactivity;
	}
	
	
}
