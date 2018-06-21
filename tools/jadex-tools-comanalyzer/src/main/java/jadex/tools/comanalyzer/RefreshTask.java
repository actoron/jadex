package jadex.tools.comanalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import jadex.commons.SUtil;
import jadex.commons.future.Future;


/**
 * The refresh task. This class invokes the messageAdded notification on a
 * different awt thread and waits for its return to measure the duration for the
 * update. With the duration list one can adjust the time for the refresh rate.
 */
public class RefreshTask extends TimerTask
{

	/** RefreshTask */
	private final ComanalyzerPlugin plugin;

	/** This task is scheduled for execution. */
	static final int SCHEDULED = 0;

	/**
	 * This task has been cancelled (with a call to RefreshTask.cancel) and
	 * there is still one scheduled execution to be done.
	 */
	static final int CANCELLED = 1;

	/** This task has been cancelled and the last run has been executed. */
	static final int DONE = 2;

	/** The state of this task. */
	int state = SCHEDULED;

	/** This object is used to control access to the mesagelist. */
	private final Object lock = new Object();

	/** The list of the durations of the run. */
	private List durations = new ArrayList();

	/**
	 * The list of messages to be processed. The access to the attribute
	 * messages is always synchronized.
	 */
	private List messages = new ArrayList();

	/** Stores the scheduled period set in constructor */
	private long period;

	/**
	 * Create a new RefreshTask.
	 * 
	 * @param period The period this task is scheduled in the timer.
	 * @param plugin The ComanalyzerPlugin.
	 */
	public RefreshTask(ComanalyzerPlugin plugin, long period)
	{
		this.plugin = plugin;
		this.period = period;
	}

	/**
	 * Since TimerTasks cant be rescheduled, this constructor provides the
	 * possibility to pass the durationlist from an other task to this one.
	 * 
	 * @param plugin The ComanalyzerPlugin.
	 * @param period The period this task is scheduled in the timer.
	 * @param durations The passed durationlist.
	 */
	public RefreshTask(ComanalyzerPlugin plugin, long period, List durations)
	{
		this(plugin, period);
		this.durations = durations;
	}

	/**
	 * This method is call by the plugin to pass the messages to be added.
	 * 
	 * @param messages The messages to be added.
	 */
	public void fireMessagesAdded(Message[] messages)
	{
		synchronized(lock)
		{
			this.messages.addAll(0, SUtil.arrayToList(messages));
		}
	}

	/**
	 * @return The message size.
	 */
	public int getMessageSize()
	{
		synchronized(lock)
		{
			return messages.size();
		}
	}

	/**
	 * Cancel the task. If there are messages in the messagelist left, cancel it
	 * after the next run.
	 */
	public boolean cancel()
	{
		synchronized(lock)
		{
			if(messages.size() > 0)
			{
				state = CANCELLED;
				return true;
			}
			else
			{
				state = DONE;
				return super.cancel();
			}
		}
	}

	/**
	 * Invokes the messagesAdded notification on the awt thread and meseasures
	 * the duration.
	 */
	public void run()
	{
		final Message[] msg_array;

		synchronized(lock)
		{
			// return if there are no messages to process
			if(messages.size() == 0)
				return;
			// copy messages to array and clear list
			msg_array = (Message[])this.messages.toArray(new Message[this.messages.size()]);
			this.messages.clear();
		}

		// start measuring time
		long start = System.currentTimeMillis();

		// update on awt thread
		final Future<Void>	updated	= new Future<Void>();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					plugin.getMessageList().fireMessagesAdded(msg_array);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				updated.setResult(null);
			}
		});
		
		updated.get();

		// end measuring time, add duration to durationlist
		addExecutionDuration(System.currentTimeMillis() - start);

		// System.out.println("duration " + getLastExecutionDuration() + "
		// average " + getAverageExecutionDuration(3) + " messages " +
		// messages.length + " per message "+ (double)
		// (getLastExecutionDuration()/messages.length) + " period " + period);

		// cancel RefreshTask if indicated
		if(state == CANCELLED)
			cancel();
	}

	/**
	 * @return <code>true</code> if the task is done.
	 */
	public boolean isDone()
	{
		return (state == DONE);
	}

	/**
	 * @return <code>true</code> if the task is cancelled
	 */
	public boolean isCanceld()
	{
		return (state == CANCELLED);
	}

	/**
	 * Add duration of execution to durationlist
	 * 
	 * @param duration The duration to add.
	 */
	private void addExecutionDuration(long duration)
	{
		durations.add(Long.valueOf(duration));
	}

	/**
	 * @param number The count of the last durations the average is calculated
	 * from.
	 * @return The average duration for the last <code>number</code>
	 * executions.
	 */
	public long getAverageExecutionDuration(int number)
	{
		long total = 0;
		int count = 0;

		for(int i = durations.size() - 1; i >= 0 && !(i < (durations.size() - number)); i--)
		{
			total += ((Long)durations.get(i)).longValue();
			count++;
		}
		return count > 0 ? total / count : 0;
	}

	/**
	 * @return The duration of the last execution.
	 */
	public long getLastExecutionDuration()
	{
		return ((Long)durations.get(durations.size() - 1)).longValue();
	}

	/**
	 * @return The stored period for this task.
	 */
	protected long getPeriod()
	{
		return period;
	}

	/**
	 * @return The list of durations.
	 */
	protected List getDurations()
	{
		return durations;
	}

}