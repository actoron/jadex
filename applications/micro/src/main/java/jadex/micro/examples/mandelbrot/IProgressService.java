package jadex.micro.examples.mandelbrot;

import jadex.commons.future.IFuture;

/**
 *  Interface for getting information about the progress of a task.
 */
public interface IProgressService
{
	/**
	 *  Get the progress (percentage as integer) of a given task.
	 *  @param taskid	The id of the task.
	 *  @return	A future object holding the progress as a percentage integer value.
	 */
	public IFuture<Integer> getProgress(Object taskid);
}
