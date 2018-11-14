package jadex.bdiv3.examples.alarmclock;

import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.examples.alarmclock.AlarmclockAgent.AlarmGoal;
import jadex.bdiv3.examples.alarmclock.AlarmclockAgent.NotifyGoal;
import jadex.bdiv3.examples.alarmclock.AlarmclockAgent.PlaySongGoal;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Alarm clock that notifies on alarm.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Plans({
	@Plan(trigger=@Trigger(goals=AlarmGoal.class), body=@Body(AlarmPlan.class)),
	@Plan(trigger=@Trigger(goals={PlaySongGoal.class, NotifyGoal.class}), body=@Body(PlaySongPlan.class)),
	@Plan(trigger=@Trigger(goals=NotifyGoal.class), body=@Body(BeepPlan.class)),
	@Plan(trigger=@Trigger(factadded="alarms", factremoved="alarms"), body=@Body(SyncSettingsAlarmsPlan.class))
})
@RequiredServices({
	@RequiredService(name="clockservice", type=IClockService.class),
	@RequiredService(name="tpservice", type=IThreadPoolService.class)
})
public class AlarmclockAgent
{
	//-------- attributes --------
	
	/** The filename of the alarm clock settings. */
	@AgentArgument
	protected String	settingsfile	= "./alarmclockv3_settings.ser";

	/** The loaded settings. */
	protected Settings	settings	= Settings.loadSettings(settingsfile);

	/** The gui. */
	protected ClockFrame	gui;
	
	//-------- beliefs --------
	
	/** The alarms that have been set. */
	@Belief
	protected Set<Alarm>	alarms	= new LinkedHashSet<Alarm>(Arrays.asList(settings.getAlarms()));
		
	//-------- goals --------
	
	/**
	 *  Check alarm time and trigger notification if necessary.
	 */
	@Goal(unique=true)
	public static class AlarmGoal
	{
		/** The alarm to monitor. */
		protected Alarm	alarm;
		
		/**
		 *  Create an alarm goal.
		 */
		public AlarmGoal(Alarm alarm)
		{
			this.alarm	= alarm;
		}
		
		/**
		 *  Creation condition for creating a goal for every alarm.
		 */
		@GoalCreationCondition(beliefs="alarms")
		public static AlarmGoal checkCreate(Alarm alarm)
		{
			return alarm!=null ? new AlarmGoal(alarm) : null;
		}

		/**
		 *  Get the alarm. 
		 */
		public Alarm getAlarm()
		{
			return alarm;
		}

		/**
		 *  Get the hash code.
		 */
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((alarm == null) ? 0 : alarm.hashCode());
			return result;
		}

		/**
		 *  Test for equality.
		 */
		public boolean equals(Object obj)
		{
			return obj instanceof AlarmGoal && SUtil.equals(alarm, ((AlarmGoal)obj).alarm);
		}
	}
	
	/**
	 *  Play a song
	 */
	@Goal
	public static class PlaySongGoal
	{
		/** The song to play. */
		protected URL	song;
		
		/**
		 *  Create a play song goal.
		 */
		public PlaySongGoal(URL song)
		{
			this.song	= song;
		}

		/**
		 *  Get the song.
		 */
		public URL getSong()
		{
			return song;
		}
	}

	/**
	 *  Notify about an alarm
	 */
	@Goal
	public static class NotifyGoal	extends PlaySongGoal
	{
		/** The alarm to notify about. */
		protected Alarm	alarm;
		
		/**
		 *  Create an alarm goal.
		 */
		public NotifyGoal(Alarm alarm)
		{
			super(alarm.getFilenameUrl());
			this.alarm	= alarm;
		}

		/**
		 *  Get the alarm. 
		 */
		public Alarm getAlarm()
		{
			return alarm;
		}
	}
	
	//-------- methods --------
	
	/**
	 *  Start the agent
	 */
	@AgentCreated
	public IFuture<Void>	body(IInternalAccess agent)
	{
		final Future<Void>	ret	= new Future<Void>();
		final IExternalAccess	exta	= agent.getExternalAccess();
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				AlarmclockAgent.this.gui	= new ClockFrame(exta);
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Get the settings.
	 */
	public Settings	getSettings()
	{
		return settings;
	}

	/**
	 *  Set the settings and update the alarms.
	 */
	public void setSettings(Settings settings)
	{
		alarms.clear();
		alarms.addAll(Arrays.asList(settings.getAlarms()));
		this.settings	= settings;
	}
	
	/**
	 *  Get the gui.
	 */
	public ClockFrame	getGui()
	{
		return gui;
	}
	
	/**
	 *  Add an alarm.
	 */
	public void	addAlarm(Alarm alarm)
	{
		alarms.add(alarm);
	}
	
	/**
	 *  Remove an alarm.
	 */
	public void	removeAlarm(Alarm alarm)
	{
		alarms.remove(alarm);
	}
	
	/**
	 *  Get the alarms
	 */
	public Alarm[]	getAlarms()
	{
		return alarms.toArray(new Alarm[alarms.size()]);
	}
}
