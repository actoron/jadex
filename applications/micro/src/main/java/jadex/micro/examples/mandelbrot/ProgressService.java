package jadex.micro.examples.mandelbrot;

import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Progress service implementation.
 */
@Service
public class ProgressService implements IProgressService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected CalculateAgent agent;
	
	//-------- methods --------
	
	/**
	 *  Get the progress (percentage as integer) of a given task.
	 *  @param taskid	The id of the task.
	 *  @return	A future object holding the progress as a percentage integer value.
	 */
	public IFuture getProgress(Object taskid)
	{
		int	ret;
		
		// Task in progress.
		if(taskid.equals(agent.getTaskId()))
		{
//			System.out.println("progress: "+agent.getProgress());
			ret	= agent.getProgress();
		}
		
		// Task not yet started.
		else
		{
//			System.out.println("no progress: "+taskid+", "+agent.getTaskId());
			ret	= 0;
		}
		
		return new Future(Integer.valueOf(ret));
	}
}
