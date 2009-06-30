package jadex.bpmn.model;

/**
 * 
 */
public class MSequenceEdge extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The outgoing edges. */
	protected MVertex source;
	
	/** The incoming edges. */
	protected MVertex target;
	
	/** The type. */
	protected String type;
	
	//-------- constructors --------
	
	//-------- methods --------

	/**
	 * 
	 */
	public MVertex getSource()
	{
		return source;
	}
	
	/**
	 * 
	 */
	public MVertex getTarget()
	{
		return target;
	}
	
	/**
	 * 
	 */
	public void setSource(MVertex source)
	{
		this.source = source;
	}
	
	/**
	 * 
	 */
	public void setTarget(MVertex target)
	{
		this.target = target;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type)
	{
		this.type = type;
	}

}
