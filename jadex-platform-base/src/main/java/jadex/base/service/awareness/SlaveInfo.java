package jadex.base.service.awareness;

/**
 * 
 */
public class SlaveInfo
{
	/** The receiver port. */
	protected int port;

	/**
	 *  Create a new slave info.
	 */
	public SlaveInfo()
	{
	}
	
	/**
	 *  Create a new slave info.
	 */
	public SlaveInfo(int port)
	{
		this.port = port;
	}

	/**
	 *  Get the port.
	 *  @return the port.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 *  Set the port.
	 *  @param port The port to set.
	 */
	public void setPort(int port)
	{
		this.port = port;
	}
}
