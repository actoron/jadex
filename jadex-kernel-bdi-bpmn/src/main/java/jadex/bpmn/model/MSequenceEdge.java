package jadex.bpmn.model;

/**
 * 
 */
public class MSequenceEdge extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The outgoing edges. */
	protected MActivity source;
	
	/** The incoming edges. */
	protected MActivity target;
	
	/** The type. */
	protected String type;
	
	//-------- constructors --------
	
	//-------- methods --------

	/**
	 * 
	 */
	public MActivity getSource()
	{
		return source;
	}
	
	/**
	 * 
	 */
	public MActivity getTarget()
	{
		return target;
	}
	
	/**
	 * 
	 */
	public void setSource(MActivity source)
	{
		this.source = source;
	}
	
	/**
	 * 
	 */
	public void setTarget(MActivity target)
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
