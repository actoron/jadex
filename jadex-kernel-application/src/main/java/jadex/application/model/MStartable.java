package jadex.application.model;

/**
 *  Base class for startable elements.
 */
public class MStartable
{
	//-------- attributes --------
	
	/** The suspend flag. */
	protected Boolean suspend;
	
	/** The master flag. */
	protected Boolean master;
	
	/** The daemon flag. */
	protected Boolean daemon;
	
	/** The autoshutdown flag. */
	protected Boolean autoshutdown;
	
	//-------- methods --------
	
	/**
	 *  Get the master.
	 *  @return the master.
	 */
	public Boolean getMaster()
	{
		return master;
	}

	/**
	 *  Set the master.
	 *  @param master The master to set.
	 */
	public void setMaster(Boolean master)
	{
		this.master = master;
	}

	/**
	 *  Get the daemon.
	 *  @return the daemon.
	 */
	public Boolean getDaemon()
	{
		return daemon;
	}

	/**
	 *  Set the daemon.
	 *  @param daemon The daemon to set.
	 */
	public void setDaemon(Boolean daemon)
	{
		this.daemon = daemon;
	}

	/**
	 *  Get the autoshutdown.
	 *  @return the autoshutdown.
	 */
	public Boolean getAutoShutdown()
	{
		return autoshutdown;
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoShutdown(Boolean autoshutdown)
	{
		this.autoshutdown = autoshutdown;
	}

	/**
	 *  Get the suspend.
	 *  @return the suspend.
	 */
	public Boolean getSuspend()
	{
		return suspend;
	}

	/**
	 *  Set the suspend.
	 *  @param suspend The suspend to set.
	 */
	public void setSuspend(Boolean suspend)
	{
		this.suspend = suspend;
	}
}
