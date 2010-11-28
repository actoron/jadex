package jadex.micro.examples.mandelbrot;

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
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		addService(new CalculateService(this));
	}
	
	/**
	 *  Execute the body.
	 */
	public void executeBody()
	{
		IComponentStep step = new IComponentStep()
		{
			public Object execute(IInternalAccess ia)
			{
				if(!isHadJob())
					killComponent();
				setHadJob(false);
				waitFor(5000, this);
				return null;
			}
		};
		waitFor(5000, step);
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
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("Agent offering a calculate service.", null, null,
			null, null, null,
			new Class[]{}, new Class[]{ICalculateService.class});
	}
}
