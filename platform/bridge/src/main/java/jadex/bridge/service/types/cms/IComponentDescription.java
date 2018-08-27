package jadex.bridge.service.types.cms;

import jadex.bridge.Cause;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;


/**
 *  A description of a component, i.e. information
 *  about the execution state, component type, etc.
 */
public interface IComponentDescription
{
	//-------- constants --------
	
	// todo: remove? needed for JADE
//	/** Predefined value "initiated" for slot state. */
//	public static final String  STATE_INITIATED  = "initiated";
//	/** Predefined value "transit" for slot state. */
//	public static final String  STATE_TRANSIT  = "transit";	
	
	/** Predefined value "active" for slot state. */
	public static final String  STATE_ACTIVE  = "active";
	/** Predefined value "suspended" for slot state. */
	public static final String  STATE_SUSPENDED  = "suspended";
//	/** Predefined value "terminating" for slot state. */
//	public static final String  STATE_TERMINATING  = "terminating";
	/** Predefined value "terminated" for slot state. */
	public static final String  STATE_TERMINATED  = "terminated";
	
//	/** The ready processing state. */
//	public static final String	PROCESSINGSTATE_READY	= "ready";
//	/** The running processing state. */
//	public static final String	PROCESSINGSTATE_RUNNING	= "running";
//	/** The idle processing state. */
//	public static final String	PROCESSINGSTATE_IDLE	= "idle";
	
	//-------- methods --------

	/**
	 *  Get the identifier of the component.
	 *  @return The component identifier.
	 */
	public IComponentIdentifier getName();
	
	/**
	 *  Get the model name.
	 *  @return The model name.
	 */
	public String getModelName();
	
	/**
	 *  Get the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier();
	
	/**
	 *  Get the ownership string of the component.
	 *  @return The ownership string.
	 */
	public String getOwnership();

	/**
	 *  Get the component type.
	 *  @return The component type name (e.g. 'BDI Agent').
	 */
	public String getType();
	
	/**
	 *  Get the execution state of the component.
	 *  E.g. active or suspended.
	 *  @return The state.
	 */
	public String getState();
	
//	/**
//	 *  Get the processing state of the component.
//	 *  I.e. ready, running or blocked.
//	 *  @return The processing state.
//	 */
//	public String getProcessingState();

	//-------- internal properties not used for search --------
	
	/**
	 *  Get the creation time.
	 *  @return The creation time.
	 */
	public long getCreationTime();

	/**
	 *  Get the creator of this component. 
	 *  (parent in case of child or external in case of top-level component).
	 */
	public IComponentIdentifier getCreator();
	
//	/**
//	 *  Get the cause for creating the component.
//	 *  @return The cause.
//	 */
//	public Cause getCause();
	
	/**
	 *  Get the enabled breakpoints (if any).
	 *  @return The enabled breakpoints.
	 */
	public String[]	getBreakpoints();
	
//	/**
//	 *  Is the component a master.
//	 *  When a master component is killed the parent component will be killed two.
//	 *  @return True, if master component.
//	 */
//	public boolean isMaster();

//	/**
//	 *  Is the component a daemon.
//	 *  When platform is in autoshutdown mode and the last non-daemon component is killed
//	 *  the platform will also be shutdowned.
//	 *  @return True, if daemon component.
//	 */
//	public boolean isDaemon();
	
//	/**
//	 *  Get the auto shutdown flag.
//	 *  @return	The flag.
//	 */
//	public boolean isAutoShutdown();

	/**
	 *  Get the synchronous flag.
	 *  @return	The synchronous flag.
	 */
	public boolean isSynchronous();
	
//	/**
//	 *  Get the persistable flag.
//	 *  @return	The persistable flag.
//	 */
//	public boolean isPersistable();
	
	/**
	 *  Get the monitoring flag.
	 *  @return	The monitoring flag.
	 */
	public PublishEventLevel getMonitoring();
	
	/**
	 *  Get the local type name of a component (defined in the parent).
	 *  @return The local type.
	 */
	public String getLocalType();
	
	/**
	 *  Test if it is a system component.
	 *  @return True, if it is a system component.
	 */
	public boolean isSystemComponent();
}
