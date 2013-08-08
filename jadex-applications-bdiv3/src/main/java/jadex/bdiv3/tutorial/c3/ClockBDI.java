package jadex.bdiv3.tutorial.c3;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Description;

import java.text.SimpleDateFormat;

/**
 *  Belief with update rate.
 */
@Description("The clock agent C2. <br>  This translation agent uses a belief with update rate.")
@Agent
@Service
public class ClockBDI
{
	/** The current time. */
	@Belief(updaterate=1000)
	protected long time = System.currentTimeMillis();

	/** The date formatter. */
	public SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

	/**
	 *  Plan that prints the time.
	 */
	@Plan(trigger=@Trigger(factchangeds="time"))
	protected void printTime()
	{
		System.out.println(formatter.format(time));
	}
}
