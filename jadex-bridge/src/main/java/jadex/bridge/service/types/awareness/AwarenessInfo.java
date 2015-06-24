package jadex.bridge.service.types.awareness;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.ITransportComponentIdentifier;
import jadex.bridge.VersionInfo;
import jadex.commons.SUtil;

import java.util.HashMap;
import java.util.Map;

/**
 *  Simple info object that is sent between awareness agents.
 */
public class AwarenessInfo
{
	//-------- constants --------
	
	/** State indicating that a component is currently online. */
	public static final String	STATE_ONLINE	= "online";
	
	/** State indicating that a component is going offline. */
	public static final String	STATE_OFFLINE	= "offline";
	
	/** Hack. Indicate that the underlying awareness mechanism died. */
	public static final String	STATE_ALLOFFLINE	= "alloffline";
	
	/** The property for the Jadex version. */
	public static final String	PROPERTY_JADEXVERSION	= "jadex.version";
	
	/** The property for the Jadex build date. */
	public static final String	PROPERTY_JADEXDATE	= "jadex.date";
	
	/** The mechanism src. */
	public static final String	PROPERTY_AWAMECHANISM	= "awamechanism";

	
	/** The system properties to send in awareness infos. */
	protected static final String[]	SYSTEM_PROPERTIES	= new String[]
	{
		"os.name",						//	e.g. Windows 7
		"os.version",					//	e.g. 6.1
		"os.arch",						//	e.g. amd64
		"java.specification.version",	//	e.g. 1.7
		"java.vendor",					//	e.g. Oracle Corporation
	};
	// cf. System.getProperties().list(System.out);
	
	//-------- attributes --------
	
	/** The sending component's identifier. */
	protected ITransportComponentIdentifier	sender;

	/** The component state. */
	protected String state;
	
	/** The current send time delay (interval). */
	protected long	delay;
	
	/** The includes list. */
	protected String[] includes;
	
	/** The excludes list. */
	protected String[] excludes;

	/** The masterid (or null if not a master). */
	protected String masterid;
	
	/** A map of properties (if any). */
	protected Map<String, String>	properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo()
	{
	}
	
//	/**
//	 *  Create a new awareness info.
//	 */
//	public AwarenessInfo(IComponentIdentifier sender, String state, long delay, String mechsrc)
//	{
//		this(sender, state, delay, null, null, null, mechsrc);
//	}
	
	/**
	 *  Create a new awareness info.
	 */
	public AwarenessInfo(ITransportComponentIdentifier sender, String state, long delay, 
		String[] includes, String[] excludes, String masterid, String mechsrc)
	{
		this.sender = sender;
		this.state = state;
		this.delay = delay;
		this.includes	= includes!=null? includes.clone(): null;
		this.excludes	= excludes!=null? excludes.clone(): null;
		this.masterid = masterid;
		
		// fill in platform properties (todo: only fill in for some infos?)
		this.properties	= new HashMap<String, String>();
		VersionInfo versionInfo = VersionInfo.getInstance();
		properties.put(PROPERTY_JADEXVERSION, versionInfo.getVersion());
		properties.put(PROPERTY_JADEXDATE, String.valueOf(versionInfo.getDate() != null ? versionInfo.getDate().getTime() : 0));
		for(String prop: SYSTEM_PROPERTIES)
		{
			properties.put(prop, System.getProperty(prop));
		}
		
		properties.put(PROPERTY_AWAMECHANISM, mechsrc);
	}
	
	//-------- methods --------

	/**
	 *  Get the sender.
	 *  @return the sender.
	 */
	public ITransportComponentIdentifier getSender()
	{
		return sender;
	}

	/**
	 *  Set the sender.
	 *  @param sender The sender to set.
	 */
	public void setSender(ITransportComponentIdentifier sender)
	{
		this.sender = sender;
	}
	
	/**
	 *  Get the state.
	 *  @return the state.
	 */
	public String	getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state The state to set.
	 */
	public void setState(String state)
	{
		this.state = state;
	}

	/**
	 *  Get the delay.
	 *  @return the delay.
	 */
	public long getDelay()
	{
		return delay;
	}

	/**
	 *  Set the delay.
	 *  @param delay The delay to set.
	 */
	public void setDelay(long delay)
	{
		this.delay = delay;
	}
	
	/**
	 *  Get the includes.
	 *  @return the includes.
	 */
	public String[] getIncludes()
	{
		return includes!=null? includes: SUtil.EMPTY_STRING_ARRAY;
	}

	/**
	 *  Set the includes.
	 *  @param includes The includes to set.
	 */
	public void setIncludes(String[] includes)
	{
		this.includes	= includes!=null? includes.clone(): null;
	}

	/**
	 *  Set the excludes.
	 *  @param excludes The excludes to set.
	 */
	public void setExcludes(String[] excludes)
	{
		this.excludes	= excludes!=null? excludes.clone(): null;
	}
	
	/**
	 *  Get the excludes.
	 *  @return the excludes.
	 */
	public String[] getExcludes()
	{
		return excludes!=null? excludes: SUtil.EMPTY_STRING_ARRAY;
	}
	
	/**
	 *  Get the masterid.
	 *  @return the masterid.
	 */
	public String getMasterId()
	{
		return masterid;
	}

	/**
	 *  Set the masterid.
	 *  @param masterid The masterid to set.
	 */
	public void setMasterId(String masterid)
	{
		this.masterid = masterid;
	}
	
	/**
	 *  Get the properties.
	 *  @return The properties, if any.
	 */
	public Map<String, String>	getProperties()
	{
		return properties;
	}
	
	/**
	 *  Set the properties.
	 *  @param props The properties.
	 */
	public void	setProperties(Map<String, String> props)
	{
		this.properties	= props;
	}
	

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "AwarenessInfo(sender="+sender+", state="+state+", delay="+delay+")";
	}
}
