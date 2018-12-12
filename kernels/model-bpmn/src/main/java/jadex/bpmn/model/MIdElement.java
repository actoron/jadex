package jadex.bpmn.model;

import jadex.commons.IdGenerator;

/**
 *  Base model element with an id. 
 */
public class MIdElement
{
	/** ID generator. */
	protected static final IdGenerator ID_GENERATOR = new IdGenerator();
	
	//-------- attributes --------
	
	/** The id. */
	protected String id;
	
	/**
	 *  Generates an ID element.
	 */
	protected MIdElement()
	{
		synchronized (ID_GENERATOR)
		{
			id = ID_GENERATOR.generateId();
		}
	}

	//-------- methods ---------
	
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
	 * 
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/**
	 * 
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof MIdElement && ((MIdElement)obj).getId().equals(getId());
	}
}
