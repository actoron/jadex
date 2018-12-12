package jadex.bridge.modelinfo;

import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;

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
	
	/** The keepalive flag. */
	private Boolean keepalive;
	
//	/** The master flag. */
//	private Boolean master;
	
//	/** The daemon flag. */
//	private Boolean daemon;
	
//	/** The autoshutdown flag. */
//	private Boolean autoshutdown;

	/** The monitoring flag. */
	private PublishEventLevel monitoring;
	
	/** The synchronous flag. */
	private Boolean synchronous;
	
//	/** The persistable flag. */
//	private Boolean persistable;
	
	/** The scope. */
	private ServiceScope scope;
	
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
	
//	/**
//	 *  Get the master.
//	 *  @return the master.
//	 */
//	public Boolean getMaster()
//	{
//		return master;
//	}
//
//	/**
//	 *  Set the master.
//	 *  @param master The master to set.
//	 */
//	public void setMaster(Boolean master)
//	{
//		this.master = master;
//	}
//
//	/**
//	 *  Get the daemon.
//	 *  @return the daemon.
//	 */
//	public Boolean getDaemon()
//	{
//		return daemon;
//	}
//
//	/**
//	 *  Set the daemon.
//	 *  @param daemon The daemon to set.
//	 */
//	public void setDaemon(Boolean daemon)
//	{
//		this.daemon = daemon;
//	}

//	/**
//	 *  Get the autoshutdown.
//	 *  @return the autoshutdown.
//	 */
//	public Boolean getAutoShutdown()
//	{
//		return autoshutdown;
//	}
//
//	/**
//	 *  Set the autoshutdown.
//	 *  @param autoshutdown The autoshutdown to set.
//	 */
//	public void setAutoShutdown(Boolean autoshutdown)
//	{
//		this.autoshutdown = autoshutdown;
//	}

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
	 *  Get the keepalive.
	 *  @return the keepalive.
	 */
	public Boolean getKeepalive()
	{
		return keepalive;
	}

	/**
	 *  Set the keepalive.
	 *  @param keepalive The keepalive to set.
	 */
	public void setKeepalive(Boolean keepalive)
	{
		this.keepalive = keepalive;
	}
	
	/**
	 *  Get the synchronous.
	 *  @return The synchronous.
	 */
	public Boolean getSynchronous()
	{
		return synchronous;
	}

	/**
	 *  Set the synchronous.
	 *  @param synchronous The synchronous to set.
	 */
	public void setSynchronous(Boolean synchronous)
	{
		this.synchronous = synchronous;
	}
	
//	/**
//	 *  Get the persistable.
//	 *  @return The persistable.
//	 */
//	public Boolean getPersistable()
//	{
//		return persistable;
//	}
//
//	/**
//	 *  Set the persistable flag.
//	 *  @param persistable The persistable flag to set.
//	 */
//	public void setPersistable(Boolean persistable)
//	{
//		this.persistable = persistable;
//	}
	
	/**
	 *  Get the monitoring.
	 *  @return The monitoring.
	 */
	public PublishEventLevel getMonitoring()
	{
//		return monitoring==null? PublishEventLevel.OFF: monitoring;
		return monitoring;
	}

	/**
	 *  Set the monitoring.
	 *  @param monitoring The monitoring to set.
	 */
	public void setMonitoring(PublishEventLevel monitoring)
	{
		this.monitoring = monitoring;
	}

	/**
	 *  Get the scope.
	 *  @return The scope
	 */
	public ServiceScope getScope()
	{
		return scope;
	}

	/**
	 *  Set the scope.
	 *  @param scope The scope to set
	 */
	public void setScope(ServiceScope scope)
	{
		this.scope = scope;
	}
}
