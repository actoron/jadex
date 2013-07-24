package jadex.bpmn.model;

import jadex.bpmn.model.io.IdGenerator;

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
}
