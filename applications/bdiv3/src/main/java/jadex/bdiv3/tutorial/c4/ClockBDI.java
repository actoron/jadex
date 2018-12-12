package jadex.bdiv3.tutorial.c4;

import java.text.SimpleDateFormat;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

/**
 *  Belief with update rate.
 */
@Description("The clock agent C4. <br>  This agent uses a getter/setter non-field belief.")
@Agent(type=BDIAgentFactory.TYPE)
public class ClockBDI
{
	/** The date formatter. */
	public SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body(IInternalAccess agent)
	{
		// Stop the agent after 5 seconds.
		agent.getFeature(IExecutionFeature.class).waitForDelay(5000).get();
		return IFuture.DONE;
	}
	
	/**
	 *  Plan that prints the time.
	 */
	@Plan(trigger=@Trigger(factchanged="time"))
	protected void printTime()
	{
		System.out.println(formatter.format(getTime()));
	}

	/**
	 *  Get the time.
	 *  @return The time.
	 */
	@Belief(updaterate=1000)
	public long getTime()
	{
		return System.currentTimeMillis();
	}

//	/**
//	 *  Set the time.
//	 *  @param time The time to set.
//	 */
//	@Belief
//	public void setTime()
//	{
//	}
}
