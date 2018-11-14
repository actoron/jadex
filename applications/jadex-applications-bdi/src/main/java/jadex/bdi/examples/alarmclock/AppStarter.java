package jadex.bdi.examples.alarmclock;

import jadex.base.Starter;

/**
 *  Class for starting the alarmclock application.
 */
public class AppStarter
{
	/**
	 *  Main for starting Jadex with the alarmclock.
	 */
	public static void main(String[] args)
	{
		Starter.main(new String[]{"-gui", "false", "-component", "jadex/bdi/examples/alarmclock/Alarmclock.agent.xml"});
	}
}
