package jadex.bdiv3.runtime.impl;

/**
 *  Abstraction for rplans and rgoals that act as plan.
 */
public interface IInternalPlan
{
	/**
	 *  Get the candidate.
	 *  @return The candidate.
	 */
	public Object getCandidate();
	
	/**
	 *  Test if plan has passed.
	 */
	public boolean isPassed();
	
	/**
	 *  Test if plan has failed.
	 */
	public boolean isFailed();
	
	/**
	 *  Get the exception.
	 */
	public Exception getException();
}
