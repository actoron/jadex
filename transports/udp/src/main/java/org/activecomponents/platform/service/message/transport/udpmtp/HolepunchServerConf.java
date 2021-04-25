package org.activecomponents.platform.service.message.transport.udpmtp;

/**
 *  Configuration for the holepunching server.
 *
 */
public class HolepunchServerConf
{
	/** Basic command-based type. */
	public static final int BASIC = 0;
	
	/** HTTP-based type. */
	public static final int HTTP = 1;
	
	/** HTTPS-based type. */
	public static final int HTTPS = 2;
	
	/** Host of the holepunch server. */
	protected String host;
	
	/** Port of the holepunch server. */
	protected int port;
	
	/** Type of holepunch server. */
	protected int type;
	
	/**
	 *  Creates the holepunching server configuration.
	 * 
	 * @param host Host of the holepunch server.
	 * @param port Port of the holepunch server.
	 * @param web Flag if the server is web-based.
	 */
	public HolepunchServerConf(String host, int port, int type)
	{
		this.host = host;
		this.port = port;
		this.type = type;
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
	 *  Gets the type.
	 *  @return The type.
	 */
	public int getType()
	{
		return type;
	}

	
}
