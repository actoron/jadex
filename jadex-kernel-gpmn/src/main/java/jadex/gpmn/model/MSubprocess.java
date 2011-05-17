package jadex.gpmn.model;

public class MSubprocess
{
	/** The id. */
	protected String id;
	
	/** The name. */
	protected String name;
	
	/** The process reference */
	protected String processreference;
	
	/** Internal process flag */
	protected boolean internal;
	
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
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the processreference.
	 *  @return The processreference.
	 */
	public String getProcessReference()
	{
		return processreference;
	}

	/**
	 *  Set the processreference.
	 *  @param processreference The processreference to set.
	 */
	public void setProcessReference(String processreference)
	{
		this.processreference = processreference;
	}

	/**
	 *  Get the internal flag.
	 *  @return The internal flag.
	 */
	public boolean isInternal()
	{
		return internal;
	}

	/**
	 *  Set the internal flag.
	 *  @param internal The internal flag to set.
	 */
	public void setInternal(boolean internal)
	{
		this.internal = internal;
	}
}
