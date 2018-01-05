package jadex.bridge.service.types.registry;

import java.util.HashSet;
import java.util.Set;

import jadex.bridge.IComponentIdentifier;

/**
 *  The abstract registry event.
 */
public abstract class ARegistryEvent
{	
	/** Constants for identifying the different kinds of registry clients. */
	public static final String CLIENTTYPE_CLIENT = "client";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL0 = "superpeer_0";
	public static final String CLIENTTYPE_SUPERPEER_LEVEL1 = "superpeer_1";
	
	/** The number of events that must have occured before a remote message is sent. */
	protected int eventslimit;
	
	/** The timestamp of the first event (change). */
	protected long timestamp; 
	
	/** The time limit. */
	protected long timelimit;
	
	/** The clients. */
	protected Set<IComponentIdentifier> clients;
	
	/**
	 *  Create a new event.
	 *  @param eventslimit
	 *  @param timestamp
	 *  @param timelimit
	 */
	public ARegistryEvent()
	{
	}
	
	/**
	 *  Create a new event.
	 *  @param eventslimit
	 *  @param timestamp
	 *  @param timelimit
	 */
	public ARegistryEvent(int eventslimit, long timelimit)
	{
		this.eventslimit = eventslimit;
		this.timelimit = timelimit;
	}

	/**
	 * Returns the number of elements added to this event.
	 */
	public abstract int size();
	
	/**
	 *  Check if this event is due and should be sent.
	 *  @param True, if the event is due and should be sent.
	 */
	public boolean isDue()
	{
		int size = size();
		// Send event if more than eventlimit events have been collected
		// OR
		// timeout has been reached (AND and at least one event has been collected)
		// The last aspect is not used because lease times are used
		// so an empty event at least renews the lease
		return size>=getEventslimit() || (System.currentTimeMillis()-getTimestamp()>=getTimelimit());// && size>0);
	}

	/**
	 *  Get the time until this event is due.
	 *  @return The time until the event is due.
	 */
	public long getTimeUntilDue()
	{
		long wait = timelimit-(System.currentTimeMillis()-timestamp);
		return wait>0? wait: 0;
	}

	/**
	 *  Get the eventslimit.
	 *  @return The eventslimit
	 */
	public int getEventslimit()
	{
		return eventslimit;
	}

	/**
	 *  Get the timestamp.
	 *  @return The timestamp
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	/**
	 *  Get the timelimit.
	 *  @return The timelimit
	 */
	public long getTimelimit()
	{
		return timelimit;
	}
	
	/**
	 *  Get the clients.
	 *  @return The clients
	 */
	public Set<IComponentIdentifier> getClients()
	{
		return clients;
	}

	/**
	 *  Set the clients.
	 *  @param clients the clients to set
	 */
	public void setClients(Set<IComponentIdentifier> clients)
	{
		this.clients = clients;
	}
	
	/**
	 *  Add a client.
	 *  @param client The client.
	 */
	public void addClient(IComponentIdentifier client)
	{
		if(clients==null)
			clients = new HashSet<IComponentIdentifier>();
		clients.add(client);
	}

}
