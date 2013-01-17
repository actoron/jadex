package jadex.bdiv3.model;


/**
 *  Goal model.
 */
public class MGoal extends MClassBasedElement
{
	/** Never exclude plan candidates from apl. */
	public static final String EXCLUDE_NEVER = "never";

	/** Exclude tried plan candidates from apl. */ 
	public static final String EXCLUDE_WHEN_TRIED = "when_tried";
	
	/** Exclude failed plan candidates from apl. */
	public static final String EXCLUDE_WHEN_FAILED = "when_failed";

	/** Exclude succeeded plan candidates from apl. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when_succeeded";

	
	/** The retry flag. */
	protected boolean retry;
	
	/** The recur flag. */
	protected boolean recur;
	
	/** The retry delay. */
	protected long retrydelay;
	
	/** The recur delay. */
	protected long recurdelay;
	
	/** The procedual success flag. */
	protected boolean succeedonpassed;
	
	/** The unique. */
	protected boolean unique;
	
	/** The deliberation. */
	protected MDeliberation deliberation;
	
	/**
	 *  Create a new belief.
	 */
	public MGoal(String target, boolean posttoall, boolean randomselection, String excludemode,
		boolean retry, boolean recur, long retrydelay, long recurdelay, 
		boolean succeedonpassed, boolean unique, MDeliberation deliberation)
	{
		super(target, posttoall, randomselection, excludemode);
		this.retry = retry;
		this.recur = recur;
		this.retrydelay = retrydelay;
		this.recurdelay = recurdelay;
		this.succeedonpassed = succeedonpassed;
		this.unique = unique;
		this.deliberation = deliberation;
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
	 *  Get the succeed on passed.
	 *  @return The succeedonpassed.
	 */
	public boolean isSucceedOnPassed()
	{
		return succeedonpassed;
	}

	/**
	 *  Set the succeed on passed.
	 *  @param succeedonpassed The succeedonpassed to set.
	 */
	public void setSucceedOnPassed(boolean succeedonpassed)
	{
		this.succeedonpassed = succeedonpassed;
	}

	/**
	 *  Get the unique.
	 *  @return The unique.
	 */
	public boolean isUnique()
	{
		return unique;
	}

	/**
	 *  Set the unique.
	 *  @param unique The unique to set.
	 */
	public void setUnique(boolean unique)
	{
		this.unique = unique;
	}

	/**
	 *  Get the deliberation.
	 *  @return The deliberation.
	 */
	public MDeliberation getDeliberation()
	{
		return deliberation;
	}
}
