package jadex.bdiv3.tutorial.c3;

import java.text.SimpleDateFormat;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;

/**
 *  Getter/setter belief.
 */
@Description("The clock agent C3. <br>  This agent uses a getter/setter belief.")
@Agent
public class ClockBDI
{
	/** The time. */
	protected long time;
	
	/** The date formatter. */
	public SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		setTime(System.currentTimeMillis());
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
	@Belief
	public long getTime()
	{
		return time;
	}

	/**
	 *  Set the time.
	 *  @param time The time to set.
	 */
	@Belief
	public void setTime(long time)
	{
		this.time = time;
	}
}
