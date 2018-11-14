package org.activecomponents.platform.service.message.transport.udpmtp;

import org.activecomponents.udp.UdpConnectionHandler;

/**
 *  Struct for decoding the URL of a remote host.
 *
 */
public class RemoteHost
{
	/** The host URL */
	protected String url;
	
	/** Flag for hole punching. */
	protected boolean holepunching;
	
	/** The schema. */
	protected String schema;
	
	/** The host. */
	protected String host;
	
	/** The port. */
	protected int port;
	
	/** The handler. */
	protected UdpConnectionHandler handler;
	
	/**
	 *  Creates a host from an url.
	 *  @param url The url.
	 */
	public RemoteHost(String url)
	{
		this.url = url;
		holepunching = false;
		
		schema = url.substring(0, url.indexOf("//") + 2);
		
		url = url.substring(schema.length());
//		System.out.println("AG: " + address);
		port = -1;
		host = null;
		if (url.charAt(0) == '[')
		{
			// IPv6
			int ind = url.indexOf(']');
			host = url.substring(0, ind + 1);
			port = Integer.parseInt(url.substring(ind + 2));
		}
		else
		{
			// IPv4
			int ind = url.indexOf(':');
			host = url.substring(0, ind);
			port = Integer.parseInt(url.substring(ind + 1));
		}
//		System.out.println("New Tuple " + schema + " " + host + " " + port);
	}

	/**
	 *  Gets the url.
	 *  @return The url.
	 */
	public String getUrl()
	{
		return url;
	}
	
	/**
	 *  Gets the handler.
	 *  @return The handler.
	 */
	public UdpConnectionHandler getHandler()
	{
		return handler;
	}

	/**
	 *  Sets the handler.
	 *  @param handler The handler to set
	 */
	public void setHandler(UdpConnectionHandler handler)
	{
		this.handler = handler;
	}

	/**
	 *  Gets the schema.
	 *  @return The schema.
	 */
	public String getSchema()
	{
		return schema;
	}

	/**
	 *  Gets the host.
	 *  @return The host.
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 *  Gets the port.
	 *  @return The port.
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 *  Generates the hash code.
	 */
	public int hashCode()
	{
		return url.hashCode();
	}
	
	/**
	 *  Tests if equal.
	 */
	public boolean equals(Object obj)
	{
		if (obj instanceof RemoteHost)
			return url.equals(((RemoteHost)obj).getUrl());
		return false;
	}
}
