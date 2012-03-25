package jadex.base.service.message;

import jadex.commons.future.IFuture;

/**
 * 
 */
public class AbstractConnectionHandler
{
	/** The connection. */
	protected AbstractConnection con;
	
	/** The latest alive time. */
	protected long alivetime;
	
	/**
	 * 
	 */
	public AbstractConnectionHandler(AbstractConnection con)
	{
		this.con = con;
		this.alivetime = System.currentTimeMillis();
	}
	
	/**
	 * 
	 */
	public void setConnection(AbstractConnection con)
	{
		this.con = con;
	}
	
	/**
	 *  Set the alive time of the other connection side.
	 */
	public void setAliveTime(long alivetime)
	{
//		System.out.println("new lease: "+alivetime);
		this.alivetime = alivetime;
	}
	
	/**
	 * 
	 */
	public boolean isConnectionAlive(long lease)
	{
		boolean isalive = System.currentTimeMillis()<alivetime+lease*1.3;
//		System.out.println("alive: "+isalive+" "+alivetime+" "+System.currentTimeMillis());
		return isalive;
	}
	
	/**
	 * 
	 */
	public IFuture<Void> sendAlive()
	{
		byte type = con.isInitiatorSide()? StreamSendTask.ALIVE_INITIATOR: StreamSendTask.ALIVE_PARTICIPANT;
		return con.sendTask(con.createTask(type, null, null));
	}
	
	/**
	 * 
	 */
	public  AbstractConnection getConnection()
	{
		return con;
	}
	
	/**
	 *  Close the connection.
	 *  Notifies the other side that the connection has been closed.
	 */
	public void close()
	{
		con.close();
	}
	
	/**
	 *  Get the closed.
	 *  @return The closed.
	 */
	public boolean isClosed()
	{
		return getConnection().isClosed();
	}
	
	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getConnectionId()
	{
		return getConnection().getConnectionId();
	}
}
