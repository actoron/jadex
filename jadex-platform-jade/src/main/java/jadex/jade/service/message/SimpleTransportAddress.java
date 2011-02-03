package jadex.jade.service.message;

import jade.mtp.TransportAddress;

/**
 *  Straight forward implementation of JADE's transport address interface.
 */
public class SimpleTransportAddress implements TransportAddress
{
	//-------- attributes --------
	
	/** The proto(col). */
	protected String	proto;
	
	/** The host (name). */
	protected String	host;
	
	/** The port (number). */
	protected String	port;
	
	/** The file name (if any). */
	protected String file;
	
	/** The anchor name (if any). */
	protected String	anchor;
	
	//-------- constructors --------
	
	/**
	 *  Create a new transport address.
	 */
	public SimpleTransportAddress(String proto, String host, String port, String file, String anchor)
	{
		this.proto	= proto;
		this.host	= host;
		this.port	= port;
		this.file	= file;
		this.anchor	= anchor;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the proto.
	 */
	public String getProto()
	{
		return proto;
	}

	/**
	 *  Get the host.
	 */
	public String getHost()
	{
		return host;
	}
	
	/**
	 *  Get the port.
	 */
	public String getPort()
	{
		return port;
	}

	/**
	 *  Get the file.
	 */
	public String getFile()
	{
		return file;
	}

	/**
	 *  Get the anchor.
	 */
	public String getAnchor()
	{
		return anchor;
	}
}
