package jadex.gpmn.runtime.plan;

/** An activation target. */
public class ActivationTarget
{
	public static final class Types
	{
		public static final String GOAL = "goal";
		public static final String SUBPROCESS = "subprocess";
	}
	
	/** Activation type */
	private String type;
	
	/** Activation target */
	private String target;
	
	public ActivationTarget()
	{
		this.type = Types.GOAL;
	}
	
	public ActivationTarget(String type, String target)
	{
		this.type = type;
		this.target = target;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public String getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	
}
