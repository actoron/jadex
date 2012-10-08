package jadex.bdiv3.model;


/**
 *  Goal model.
 */
public class MGoal extends MClassBasedElement
{
	/** The retry flag. */
	protected boolean retry;
	
	/** The recur flag. */
	protected boolean recur;
	
	/** The retry delay. */
	protected long retrydelay;
	
	/** The recur delay. */
	protected long recurdelay;

	/** The exclude mode. */
	protected String excludemode;
	
	/** The rebuild mode. */
	protected boolean rebuild;

	/**
	 *  Create a new belief.
	 */
	public MGoal(Class<?> target)
	{
		super(target);
	}
	
	/**
	 *  Test if is retry.
	 *  @return True, if is retry.
	 */
	public boolean isRetry()
	{
		return retry;
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRetryDelay()
	{
		return retrydelay;
	}
	
	/**
	 *  Test if is recur.
	 *  @return True, if is recur.
	 */
	public boolean isRecur()
	{
		return recur;
	}
	
	/**
	 *  Get the retry delay.
	 *  @return The retry delay.
	 */
	public long getRecurDelay()
	{
		return recurdelay;
	}
	
	/**
	 *  Get the exlcude mode.
	 *  @return The exclude mode.
	 */
	public String getExcludeMode()
	{
		return excludemode;
	}
	
	/**
	 *  Test if rebuild APL.
	 *  @return True, if rebuild.
	 */
	public boolean isRebuild()
	{
		return rebuild;
	}
	
//	/**
//	 *  Test if goal should be unique.
//	 *  @return True, if unique.
//	 */
//	public boolean isUnique();
	
}
