package jadex.base.service.message.transport.httprelaymtp.nio;


/**
 *  A timer to be executed on the NIO selector thread.
 */
public abstract class SelectorTimer	implements Runnable
{
	//-------- static attributes --------
	
	/** The counter for generating unique ids. */
	protected static int idcount;
	
	//-------- attributes --------
	
	/** The id. */
	protected int	id;
	
	/** The scheduled time. */
	protected long	time;
	
	//-------- constructors --------
	
	/**
	 *  Create a request.
	 */
	public SelectorTimer()
	{
		synchronized(SelectorTimer.class)
		{
			this.id = idcount++;
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id.
	 */
	public int	getId()
	{
		return id;
	}
	
	/**
	 *  Get the schedule time.
	 */
	public long	getTaskTime()
	{
		return time;
	}
	
	/**
	 *  Set the task time.
	 */
	public void	setTaskTime(long time)
	{
		this.time	= time;
	}	
}