package jadex.commons.collection;

import java.util.Timer;
import java.util.TimerTask;

/**
 *  Timer based on Java java timer.
 */
public class TimerDelayRunner implements IDelayRunner
{
	/** The java timer. */
	protected Timer timer;
	
	/**
	 *  Wait for a delay.
	 *  @param delay The delay.
	 *  @param step The step.
	 */
	public Runnable waitForDelay(long delay, final Runnable step)
	{
		if(timer==null)
			timer = new Timer(true);
		
		final TimerTask tt = new TimerTask()
		{
			public void run()
			{
				step.run();
			}
		};
		
		timer.schedule(tt, delay);
		
		return new Runnable()
		{
			public void run()
			{
				tt.cancel();
			}
		};
	}
	
	/**
	 *  Cancel the timer.
	 */
	public void cancel()
	{
		if(timer!=null)
			timer.cancel();
	}
}
