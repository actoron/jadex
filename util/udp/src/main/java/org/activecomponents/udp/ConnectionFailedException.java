package org.activecomponents.udp;

public class ConnectionFailedException extends RuntimeException
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConnectionFailedException()
	{
	}
	
	public ConnectionFailedException(String message)
	{
		super(message);
	}
}
