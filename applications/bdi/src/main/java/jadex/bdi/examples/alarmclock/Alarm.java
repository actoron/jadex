package jadex.bdi.examples.alarmclock;

import java.io.Serializable;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jadex.commons.ResourceInfo;
import jadex.commons.SUtil;
import jadex.commons.beans.PropertyChangeListener;
import jadex.commons.beans.PropertyChangeSupport;

/**
 *  This class encapsulates the functionality of an alarm.
 */
public class Alarm implements Cloneable, Serializable
{
	//-------- constants --------

	/** Alarm mode timer. */
	public static final String TIMER = "Timer";

	/** Alarm mode once. */
	public static final String ONCE = "Once";

	/** Alarm mode hourly. */
	public static final String HOURLY = "Hourly";

	/** Alarm mode daily. */
	public static final String DAILY = "Daily";

	/** Alarm mode weekly. */
	public static final String WEEKLY = "Weekly";

	/** Alarm mode monthly. */
	public static final String MONTHLY = "Monthly";

	/** Alarm mode yearly. */
	public static final String YEARLY = "Yearly";

	/** All alarm modes. */
	public static final String[] ALARMS
		= new String[]{ONCE, HOURLY, DAILY, WEEKLY, MONTHLY, YEARLY, TIMER};

	/** Unknown alarmtime -> calculate. */
	public static final int UNKNOWN = -1;

	/** No alarmtime -> calculate. */
	public static final int NO_ALARM = -2;


	//-------- attributes --------

	/** The alarm mode. */
	protected String mode;

	/** The alarm sound filename. */
	protected String filename;

	/** The alaram message. */
	protected String message;

	/** The alarm time. */
	protected Time time;

	/** Cached alarm date. */
	protected long alarmdate;
	//protected long lastalarmdate;
	protected boolean calc_allowed;

	/** The active state. */
	protected boolean active;

	/** The clock. */
//	public transient IClockService clock;

	/** The helper object for bean events. */
	public transient PropertyChangeSupport pcs;
	
	//-------- constructors --------

	/**
	 *  Create a new alarm.
	 *  Bean constructor.
	 *  Clock needs to be set manually, later.
	 */
	public Alarm()
	{
		this(ONCE, new Time(), null, null, true);
	}

	/**
	 *  Create a new alarm.
	 *  @param mode The alarm mode (once, hourly, daily, weekly, monthly, yearly)
	 *  @param time The alarm time.
	 *  @param filename The alarm sound.
	 *  @param message The alarm message.
	 */
	public Alarm(String mode, Time time, String filename, String message, boolean active)//, IClockService clock)
	{
		this.mode = mode;
		this.time = time;
		this.filename = filename;
		this.message = message;
		this.active = active;
		this.alarmdate = UNKNOWN;
		this.calc_allowed = true;
//		this.clock = clock;
	}

	//-------- methods --------

	/**
	 *  Get the mode.
	 *  @return The mode.
	 */
	public String getMode()
	{
		return mode;
	}

	/**
	 *  Set the mode.
	 *  @param mode The mode.
	 */
	public void setMode(String mode)
	{
		String oldmode = this.mode;
		this.mode = mode;
		if(pcs!=null)
			pcs.firePropertyChange("mode", oldmode, mode);
	}

	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Get the filename.
	 */
	public URL getFilenameUrl()
	{
		URL ret = null;
		try
		{
			//ResourceInfo	rinfo	= SUtil.getResourceInfo0("jadex/examples/alarmclock/alarm.mp3");
			ResourceInfo	rinfo	= SUtil.getResourceInfo0(getFilename(), Alarm.class.getClassLoader());
			if(rinfo == null)
			{
				System.out.println("Resource not found: " + getFilename());
			}
			else
			{
				if(rinfo.getInputStream() != null)
				{
					rinfo.getInputStream().close();
				}
				String file = rinfo.getFilename();
				if(!file.startsWith("jar:"))
				{
					file = (file.startsWith("/") ? "file://" : "file:///") + file;
				}
				//System.out.println(file);
				ret = new URL(file);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename.
	 */
	public void setFilename(String filename)
	{
		String oldfilename = this.filename;
		this.filename = filename;
		if(pcs!=null)
			pcs.firePropertyChange("filename", oldfilename, filename);
	}

	/**
	 *  Get the message.
	 *  @return  The message.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 *  Set the message.
	 *  @param message The message.
	 */
	public void setMessage(String message)
	{
		String oldmessage = this.message;
		this.message = message;
		if(pcs!=null)
			pcs.firePropertyChange("message", oldmessage, message);
	}

	/**
	 *  Get the alarm time.
	 */
	public long getAlarmtime(long currenttime)
	{
		//System.out.println("getAlarmtime: "+getMessage());
		//Thread.dumpStack();
		//if(alarmdate==UNKNOWN || (alarmdate!=NO_ALARM && alarmdate<clock.getTime()))
		// Check that a call can't dealock through trying to enter setNextAlarmdate
		if(calc_allowed)
			setNextAlarmtime(currenttime);
		//System.out.println("alt: "+alarmdate);
		return alarmdate;
	}

	/**
	 *  Save the alarmtime.
	 *  @param alarmdate The alarmdate.
	 */
	public void setAlarmtime(long alarmdate)
	{
		long old = this.alarmdate;
		this.alarmdate = alarmdate;
		if(pcs!=null)
		{
//			System.out.println("alarmtime changed: "+this+" "+old+" "+alarmdate);
			pcs.firePropertyChange("alarmtime", Long.valueOf(old), Long.valueOf(alarmdate));
		}
	}

	/**
	 *  Indicate that an alarm occurred.
	 */
	public void triggerd()
	{
		if(mode.equals(TIMER) || mode.equals(ONCE))
		{
			setAlarmtime(NO_ALARM);
		}
		else
		{
			setAlarmtime(UNKNOWN);
			calc_allowed = true;
		}
	}

	/**
	 *  Calculate and save the next alarmtime.
	 */
	public synchronized void setNextAlarmtime(long currenttime)
	{
		// Check repeated for different threads that want to enter here.
		if(!calc_allowed)
			return;

//		long currenttime = clock.getTime();
		long ret = NO_ALARM;

		if(mode.equals(ONCE))
		{
			Calendar acal = new GregorianCalendar(time.getYear(), time.getMonth(), time.getMonthday(),
				time.getHours(), time.getMinutes(), time.getSeconds());
			//System.out.println(time.year+" "+time.month+" "+time.monthday+" "+
			//	time.hours+" "+time.minutes+" "+time.seconds);
			//System.out.println(acal.getTime()+" "+new Date(currenttime));
			if(acal.getTimeInMillis() > currenttime)
				ret = acal.getTime().getTime();
			calc_allowed = false;
		}
		else if(mode.equals(HOURLY))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.set(Calendar.MINUTE, time.getMinutes());
			acal.set(Calendar.SECOND, time.getSeconds());
			//System.out.println(acal.getTime()+" "+new Date(currenttime));
			if(acal.getTime().getTime() < currenttime)
				acal.add(Calendar.HOUR_OF_DAY, 1);
			ret = acal.getTime().getTime();
		}
		else if(mode.equals(DAILY))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.set(Calendar.HOUR_OF_DAY, time.getHours());
			acal.set(Calendar.MINUTE, time.getMinutes());
			acal.set(Calendar.SECOND, time.getSeconds());
			if(acal.getTime().getTime() < currenttime)
				acal.add(Calendar.DAY_OF_MONTH, 1);
			ret = acal.getTime().getTime();
		}
		else if(mode.equals(WEEKLY))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.set(Calendar.DAY_OF_WEEK, time.getWeekday());
			acal.set(Calendar.HOUR_OF_DAY, time.getHours());
			acal.set(Calendar.MINUTE, time.getMinutes());
			acal.set(Calendar.SECOND, time.getSeconds());
			if(acal.getTime().getTime() < currenttime)
				acal.add(Calendar.WEEK_OF_YEAR, 1);
			ret = acal.getTime().getTime();
		}
		else if(mode.equals(MONTHLY))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.set(Calendar.DAY_OF_MONTH, time.getMonthday());
			acal.set(Calendar.HOUR_OF_DAY, time.getHours());
			acal.set(Calendar.MINUTE, time.getMinutes());
			acal.set(Calendar.SECOND, time.getSeconds());
			if(acal.getTime().getTime() < currenttime)
				acal.add(Calendar.MONTH, 1);
			ret = acal.getTime().getTime();
		}
		else if(mode.equals(YEARLY))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.set(Calendar.MONTH, time.getMonth());
			acal.set(Calendar.DAY_OF_MONTH, time.getMonthday());
			acal.set(Calendar.HOUR_OF_DAY, time.getHours());
			acal.set(Calendar.MINUTE, time.getMinutes());
			acal.set(Calendar.SECOND, time.getSeconds());
			if(acal.getTime().getTime() < currenttime)
				acal.add(Calendar.YEAR, 1);
			ret = acal.getTime().getTime();
		}
		else if(mode.equals(TIMER))
		{
			Calendar acal = new GregorianCalendar();
			acal.setTime(new Date(currenttime));
			acal.add(Calendar.HOUR_OF_DAY, time.getHours());
			acal.add(Calendar.MINUTE, time.getMinutes());
			acal.add(Calendar.SECOND, time.getSeconds());
			if(acal.getTime().getTime() >= currenttime)
				ret = acal.getTime().getTime();
		}

		//System.out.println("setNextAlarmtime: "+getMessage()+" "+currenttime+" alarm: "+ret);

		calc_allowed = false;
		setAlarmtime(ret);
	}

	/**
	 *  Test if the alarm is active.
	 *  @return True, if active.
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 *  Set the alarm state.
	 *  @param active True for active.
	 */
	public void setActive(boolean active)
	{
		boolean oldactive = this.active;
		this.active = active;
		if(pcs!=null)
			pcs.firePropertyChange("active", oldactive, active);
	}

	/**
	 *  Get the alarm time.
	 *  @return The alarm time.
	 */
	public Time getTime()
	{
		return time;
	}

	/**
	 *  Set the alarm time.
	 *  @param time The alarm time.
	 */
	public void setTime(Time time)
	{
		Time oldtime = this.time;
		this.time = time;
		setAlarmtime(UNKNOWN);
		this.calc_allowed = true;
		if(pcs!=null)
			pcs.firePropertyChange("time", oldtime, time);
		//setNextAlarmtime();
	}

	/**
	 *  Set the clock.
	 * /
	public void	setClock(IClockService clock)
	{
		this.clock	= clock;
	}*/
	
	/**
	 *  Test two object for equality.
	 *  @param o The object.
	 *  @return True, if equal.
	 * /
	public boolean equals(Object o)
	{
		if(this == o)
			return true;
		if(o == null || getClass() != o.getClass())
			return false;

		Alarm alarm = (Alarm)o;
		return SUtil.equals(message, alarm.message);
	}*/

	/**
	 *  Compute the hashcode.
	 *  @return The hashcode.
	 * /
	public int hashCode()
	{
		return message.hashCode();
	}*/

	/**
	 * Creates and returns a copy of this object.  The precise meaning
	 * of "copy" may depend on the class of the object. The general
	 * intent is that, for any object <tt>x</tt>, the expression:
	 */
	protected Object clone()
	{
		Alarm ret = null;
		try
		{
			ret = (Alarm)super.clone();
			ret.pcs = null;
			ret.setTime((Time)time.clone());
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}

	//-------- property methods --------

	/**
	 * Add a PropertyChangeListener to the listener list.
	 * The listener is registered for all properties.
	 *
	 * @param listener The PropertyChangeListener to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		if(pcs==null)
			this.pcs = new PropertyChangeSupport(this);
		pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a PropertyChangeListener from the listener list.
	 * This removes a PropertyChangeListener that was registered
	 * for all properties.
	 *
	 * @param listener The PropertyChangeListener to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		if(pcs==null)
			this.pcs = new PropertyChangeSupport(this);
		pcs.removePropertyChangeListener(listener);
	}
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "Alarm(msg="+message+", alarmtime="+alarmdate+", active="+active+")"+hashCode();
	}
}
