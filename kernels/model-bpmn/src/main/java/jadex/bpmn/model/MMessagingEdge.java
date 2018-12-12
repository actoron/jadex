package jadex.bpmn.model;


/**
 *  A messaging edge is an edge describing a message flow between some sender and receiver.
 *  Used only for communication across pools.
 */
public class MMessagingEdge extends MEdge
{
	//-------- attributes --------
	
	/** The source description. */
	protected String sourcedescription;

	/** The target description. */
	protected String targetdescription;
	
	/** The type. */
	protected String type;
	
	//-------- methods --------

	/**
	 *  Helper method connecting two activities using this edge.
	 *  The previous connection is removed.
	 * 
	 *  @param source New source of the edge.
	 *  @param target New target of the edge.
	 */
	public void connect(MActivity source, MActivity target)
	{
		if (this.source != null)
		{
			this.source.removeOutgoingMessagingEdge(this);
		}
		
		if (this.target != null)
		{
			this.target.removeIncomingMessagingEdge(this);
		}
		
		this.source = source;
		this.target = target;
		
		source.addOutgoingMessagingEdge(this);
		target.addIncomingMessagingEdge(this);
	}
	
	/**
	 *  Get the xml source description.
	 *  @return The source description.
	 */
	public String getSourceDescription()
	{
		return this.sourcedescription;
	}

	/**
	 *  Set the xml source description.
	 *  @param sourcedescription The xml source description to set.
	 */
	public void setSourceDescription(String sourcedescription)
	{
		this.sourcedescription = sourcedescription;
	}

	/**
	 *  Get the xml target description.
	 *  @return The target description.
	 */
	public String getTargetDescription()
	{
		return this.targetdescription;
	}

	/**
	 *  Set the xml target Description.
	 *  @param targetdescription The target description to set.
	 */
	public void setTargetDescription(String targetdescription)
	{
		this.targetdescription = targetdescription;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

}
