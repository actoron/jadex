package jadex.tools.ontology;

//import jadex.bdi.runtime.SystemEvent; // todo???
import java.util.ArrayList;
import java.util.List;


/**
 *  Java class for concept CurrentState of jadex.tools.introspector ontology.
 */
public class CurrentState extends ToolRequest
{
	//-------- attributes ----------

	/** System events that occurred inside the observed agent. */
	protected List	systemevents;

	/** The system event types to be propagated to the tool. */
	protected List	eventtypes;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new CurrentState.
	 */
	public CurrentState()
	{
		this.systemevents = new ArrayList();
		this.eventtypes = new ArrayList();
	}

	/**
	 *  Init Constructor.
	 *  Create a new CurrentState.
	 *  Initializes the object with required attributes.
	 * @param tooltype
	 */
	public CurrentState(String tooltype)
	{
		this();
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the system-events of this CurrentState.
	 *   System events that occurred inside the observed agent.
	 * @return system-events
	 * /
	public SystemEvent[] getSystemEvents()
	{
		return (SystemEvent[])systemevents.toArray(new SystemEvent[systemevents.size()]);
	}*/

	/**
	 *  Set the system-events of this CurrentState.
	 *  System events that occurred inside the observed agent.
	 * @param systemevents the value to be set
	 * /
	public void setSystemEvents(SystemEvent[] systemevents)
	{
		this.systemevents.clear();
		for(int i = 0; i < systemevents.length; i++)
			this.systemevents.add(systemevents[i]);
	}*/

	/**
	 *  Get an system-events of this CurrentState.
	 *  System events that occurred inside the observed agent.
	 *  @param idx The index.
	 *  @return system-events
	 * /
	public SystemEvent getSystemEvent(int idx)
	{
		return (SystemEvent)this.systemevents.get(idx);
	}*/

	/**
	 *  Set a system-event to this CurrentState.
	 *  System events that occurred inside the observed agent.
	 *  @param idx The index.
	 *  @param systemevent a value to be added
	 * /
	public void setSystemEvent(int idx, SystemEvent systemevent)
	{
		this.systemevents.set(idx, systemevent);
	}*/

	/**
	 *  Add a system-event to this CurrentState.
	 *  System events that occurred inside the observed agent.
	 *  @param systemevent a value to be removed
	 * /
	public void addSystemEvent(SystemEvent systemevent)
	{
		this.systemevents.add(systemevent);
	}*/

	/**
	 *  Remove a system-event from this CurrentState.
	 *  System events that occurred inside the observed agent.
	 *  @param systemevent a value to be removed
	 *  @return  True when the system-events have changed.
	 * /
	public boolean removeSystemEvent(SystemEvent systemevent)
	{
		return this.systemevents.remove(systemevent);
	}*/


	/**
	 *  Get the event-types of this CurrentState.
	 *   The system event types to be propagated to the tool.
	 * @return event-types
	 */
	public String[] getEventTypes()
	{
		return (String[])eventtypes.toArray(new String[eventtypes.size()]);
	}

	/**
	 *  Set the event-types of this CurrentState.
	 *  The system event types to be propagated to the tool.
	 * @param eventtypes the value to be set
	 */
	public void setEventTypes(String[] eventtypes)
	{
		this.eventtypes.clear();
		for(int i = 0; i < eventtypes.length; i++)
			this.eventtypes.add(eventtypes[i]);
	}

	/**
	 *  Get an event-types of this CurrentState.
	 *  The system event types to be propagated to the tool.
	 *  @param idx The index.
	 *  @return event-types
	 */
	public String getEventType(int idx)
	{
		return (String)this.eventtypes.get(idx);
	}

	/**
	 *  Set a event-type to this CurrentState.
	 *  The system event types to be propagated to the tool.
	 *  @param idx The index.
	 *  @param eventtype a value to be added
	 */
	public void setEventType(int idx, String eventtype)
	{
		this.eventtypes.set(idx, eventtype);
	}

	/**
	 *  Add a event-type to this CurrentState.
	 *  The system event types to be propagated to the tool.
	 *  @param eventtype a value to be removed
	 */
	public void addEventType(String eventtype)
	{
		this.eventtypes.add(eventtype);
	}

	/**
	 *  Remove a event-type from this CurrentState.
	 *  The system event types to be propagated to the tool.
	 *  @param eventtype a value to be removed
	 *  @return  True when the event-types have changed.
	 */
	public boolean removeEventType(String eventtype)
	{
		return this.eventtypes.remove(eventtype);
	}

	//-------- additional methods --------

	/**
	 *  Get a string representation of this CurrentState.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "CurrentState(" + "tooltype=" + getToolType() + ")";
	}

}
