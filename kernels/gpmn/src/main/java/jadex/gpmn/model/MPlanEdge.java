package jadex.gpmn.model;

/**
 *  An edge is a connection between 
 *  two diagram elements.
 */
public class MPlanEdge
{
	/** The id. */
	protected String id;
	
	/** The source ID */
	protected String sourceid;
	
	/** The target ID */
	protected String targetid;
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return this.id;
	}

	/**
	 *  Set the id.
	 *  @param id the id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *  Get the sourceid.
	 *  @return The sourceid.
	 */
	public String getSourceId()
	{
		return sourceid;
	}

	/**
	 *  Set the sourceid.
	 *  @param sourceid The sourceid to set.
	 */
	public void setSourceId(String sourceid)
	{
		this.sourceid = sourceid;
	}

	/**
	 *  Get the targetid.
	 *  @return The targetid.
	 */
	public String getTargetId()
	{
		return targetid;
	}

	/**
	 *  Set the targetid.
	 *  @param targetid The targetid to set.
	 */
	public void setTargetId(String targetid)
	{
		this.targetid = targetid;
	}
	
	public String toString()
	{
		return "[Plan Edge " + id + " from " + sourceid + " to " + targetid + "]";
	}
}
