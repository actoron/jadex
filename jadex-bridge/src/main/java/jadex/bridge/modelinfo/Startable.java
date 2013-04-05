package jadex.bridge.modelinfo;

/**
 *  Base class for startable elements.
 */
public class Startable
{
	//-------- attributes --------
	
	/** The description. */
	protected String description;

	/** The suspend flag. */
	private Boolean suspend;
	
	/** The master flag. */
	private Boolean master;
	
	/** The daemon flag. */
	private Boolean daemon;
	
	/** The autoshutdown flag. */
	private Boolean autoshutdown;

	/** The monitoring flag. */
	private Boolean monitoring;
	
	//-------- methods --------
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}
	
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

	/**
	 *  Get the monitoring.
	 *  @return The monitoring.
	 */
	public Boolean getMonitoring()
	{
		return monitoring;
	}

	/**
	 *  Set the monitoring.
	 *  @param monitoring The monitoring to set.
	 */
	public void setMonitoring(Boolean monitoring)
	{
		this.monitoring = monitoring;
	}
}
