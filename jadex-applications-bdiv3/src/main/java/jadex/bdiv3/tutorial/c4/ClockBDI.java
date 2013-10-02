package jadex.bdiv3.tutorial.c4;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

import java.text.SimpleDateFormat;

/**
 *  Belief with update rate.
 */
@Description("The clock agent C4. <br>  This agent uses a getter/setter non-field belief.")
@Agent
public class ClockBDI
{
	/** The date formatter. */
	public SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
//		setTime();
	}
	
	/**
	 *  Plan that prints the time.
	 */
	@Plan(trigger=@Trigger(factchangeds="time"))
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
