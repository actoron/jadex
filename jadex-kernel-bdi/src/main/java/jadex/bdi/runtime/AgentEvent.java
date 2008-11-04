package jadex.bdi.runtime;


/**
 *  The agent event is used for the agent call back interface. Whenever
 *  a method of the IAgentInterface is called it will contain an agent
 *  event with detailed information about the event that happened.
 */
public class AgentEvent
{
	//-------- attributes --------
	
	/** The event source. */
	protected IElement source;
	
	/** The value. */
	protected Object value;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent event.
	 *  @param source The event source.
	 */
	public AgentEvent(IElement source)
	{
		this.source = source;
	}
	
	/**
	 *  Create a new agent event.
	 *  @param source The event source.
	 *  @param value The event value.
	 */
	public AgentEvent(IElement source, Object value)
	{
		this.source = source;
		this.value = value;
	}

	//-------- methods --------
	
	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public IElement getSource()
	{
		return source;
	}

	/**
	 *  Set the source.
	 *  @param source The source to set.
	 */
	public void setSource(IElement source)
	{
		this.source = source;
	}

	/**
	 *  Get the value.
	 *  @return The value.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 *  Set the value.
	 *  @param value The value to set.
	 */
	public void setValue(Object value)
	{
		this.value = value;
	}

	/**
	 *  Return the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
//		String sourcename = source!=null? source.getName(): ""+source;
//		return "AgentEvent(source="+sourcename+", value="+getValue()+")";
		return "AgentEvent";
	}
	
}
