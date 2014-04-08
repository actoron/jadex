package jadex.bpmn.model;

public class MEdge extends MAssociationTarget
{
	/** The edge source. */
	protected MActivity source;
	
	/** The edge target. */
	protected MActivity target;
	
	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public MActivity getSource()
	{
		return source;
	}
	
	/**
	 *  Set the source.
	 */
	public void setSource(MActivity source)
	{
		this.source = source;
	}
	
	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public MActivity getTarget()
	{
		return target;
	}
	
	/**
	 *  Set the target.
	 *  @param target The target.
	 */
	public void setTarget(MActivity target)
	{
		this.target = target;
	}
}
