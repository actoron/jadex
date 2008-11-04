package jadex.tools.ontology;

import java.util.ArrayList;
import java.util.List;


/**
 *  Java class for concept Register of jadex.tools.introspector ontology.
 */
public class Register extends ToolRequest
{
	//-------- attributes ----------

	/** The system event types to be propagated to the tool. */
	protected List	eventtypes;

	//-------- constructors --------

	/**
	 *  Default Constructor.
	 *  Create a new Register.
	 */
	public Register()
	{
		this.eventtypes = new ArrayList();
	}

	/**
	 *  Init Constructor. <br>
	 *  Create a new Register.<br>
	 *  Initializes the object with required attributes.
	 * @param tooltype
	 */
	public Register(String tooltype)
	{
		this();
		setToolType(tooltype);
	}

	//-------- accessor methods --------

	/**
	 *  Get the event-types of this Register.
	 *   The system event types to be propagated to the tool.
	 * @return event-types
	 */
	public String[] getEventTypes()
	{
		return (String[])eventtypes.toArray(new String[eventtypes.size()]);
	}

	/**
	 *  Set the event-types of this Register.
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
	 *  Get an event-types of this Register.
	 *  The system event types to be propagated to the tool.
	 *  @param idx The index.
	 *  @return event-types
	 */
	public String getEventType(int idx)
	{
		return (String)this.eventtypes.get(idx);
	}

	/**
	 *  Set a event-type to this Register.
	 *  The system event types to be propagated to the tool.
	 *  @param idx The index.
	 *  @param eventtype a value to be added
	 */
	public void setEventType(int idx, String eventtype)
	{
		this.eventtypes.set(idx, eventtype);
	}

	/**
	 *  Add a event-type to this Register.
	 *  The system event types to be propagated to the tool.
	 *  @param eventtype a value to be removed
	 */
	public void addEventType(String eventtype)
	{
		this.eventtypes.add(eventtype);
	}

	/**
	 *  Remove a event-type from this Register.
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
	 *  Get a string representation of this Register.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "Register(" + "tooltype=" + getToolType() + ")";
	}

}
