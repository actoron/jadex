package jadex.micro.examples.mandelbrot;

import jadex.bridge.Argument;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

/**
 *  Calculate agent allows calculating the colors of an area using a calculate service.
 */
public class CalculateAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** Flag indicating that the agent had a job. */
	protected boolean hadjob;
	
	/** Id of the current job. */
	protected Object	taskid;
	
	/** Progress of the current job. */
	protected int progress;
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		// Calculation service runs on component thread.
		addService(new CalculateService(this));
		
		// Progress service runs on separate thread to provide
		// progress information about running calculations.
		addDirectService(new ProgressService(this));
	}
	
	/**
	 *  Execute the body.
	 */
	public void executeBody()
	{
		final long delay = ((Long)getArgument("delay")).longValue();
		IComponentStep step = new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if(!isHadJob())
				{
					System.out.println("killComponent: "+getComponentIdentifier());
					killComponent();
				}
				setHadJob(false);
				waitFor(delay, this);
				return null;
			}
		};
		waitFor(delay, step);
	}
	
	/**
	 *  Set the hadjob.
	 *  @param hadjob The hadjob to set.
	 */
	public void setHadJob(boolean hadjob)
	{
		this.hadjob = hadjob;
	}
	
	/**
	 *  Get the hadjob.
	 *  @return The hadjob.
	 */
	public boolean isHadJob()
	{
		return hadjob;
	}
	
	/**
	 *  Get the current task id.
	 */
	public Object	getTaskId()
	{
		return taskid;
	}
	
	/**
	 *  Set the current task id.
	 */
	public void	setTaskId(Object taskid)
	{
		this.taskid	= taskid;
	}
	
	/**
	 *  Get the current progress.
	 */
	public int	getProgress()
	{
		return progress;
	}
	
	/**
	 *  Set the current progress.
	 */
	public void	setProgress(int progress)
	{
		this.progress	= progress;
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a calculate service.", null, 
			new IArgument[]{new Argument("delay", "Agent kills itself when no job arrives in the delay interval.", "Long", new Long(1000))},
			null, null, null,
			new Class[]{}, new Class[]{ICalculateService.class});
	}
}
