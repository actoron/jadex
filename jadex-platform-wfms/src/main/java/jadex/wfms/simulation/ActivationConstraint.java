package jadex.wfms.simulation;

/**
 *  Constraint for checking the number of activations.
 */
public class ActivationConstraint
{
	public static final int MODE_EQUALS			= 0;
	public static final int MODE_LESSEQUALS		= 1;
	public static final int MODE_GREATEREQUALS	= 2;
	public static final int MODE_LESS			= 4;
	public static final int MODE_GREATER		= 5;
	
	/** The mode */
	private int mode;
	
	/** The activation count */
	private int activationCount;
	
	public ActivationConstraint(int mode, int activationCount)
	{
		this.mode = mode;
		this.activationCount = activationCount;
	}
	
	/**
	 *  Get the activationCount.
	 *  @return The activationCount.
	 */
	public int getActivationCount()
	{
		return activationCount;
	}

	/**
	 *  Attempts to validate the constraint.
	 *  @param count The actual number of activations.
	 *  @return True, if the constraint is satisfied.
	 */
	public boolean isValid(int count)
	{
		switch(mode)
		{
			case MODE_EQUALS:
				return count == activationCount;
			case MODE_LESSEQUALS:
				return count <= activationCount;
			case MODE_GREATEREQUALS:
				return count >= activationCount;
			case MODE_LESS:
				return count < activationCount;
			case MODE_GREATER:
				return count > activationCount;
			default:
				return false;
		}
	}
}
