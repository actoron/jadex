package jadex.bdiv3x.runtime;

/**
 *  Dummy class for loading v2 examples using v3x.
 */
public abstract class Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public abstract void body();

	/**
	 *  The passed method is called on plan success.
	 */
	public void	passed()
	{
	}

	/**
	 *  The failed method is called on plan failure/abort.
	 */
	public void	failed()
	{
	}

	/**
	 *  The plan was aborted (because of conditional goal
	 *  success or termination from outside).
	 */
	public void aborted()
	{
	}
	
	public void	waitFor(int timeout)
	{
		// tofu
	}
	
	public void	killAgent()
	{
		// tofu
	}
}
