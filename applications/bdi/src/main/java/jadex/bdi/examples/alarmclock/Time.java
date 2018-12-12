package jadex.bdi.examples.alarmclock;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *  The alarm time struct.
 */
public class Time implements Cloneable, Serializable
{
	//-------- attributes --------

	/** The seconds. */
	private int seconds;

	/** The minutes. */
	private int minutes;

	/** The hours. */
	private int hours;

	/** The weekday. */
	private int weekday;

	/** The monthday. */
	private int monthday;

	/** The month. */
	private int month;

	/** The year. */
	private int year;

	//-------- constructors --------

	/**
	 *  Create a new time.
	 */
	public Time()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new time.
	 */
	public Time(Date date)
	{
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(date);
		this.seconds = cal.get(Calendar.SECOND);
		this.minutes = cal.get(Calendar.MINUTE);
		this.hours = cal.get(Calendar.HOUR_OF_DAY);
		this.weekday = cal.get(Calendar.DAY_OF_WEEK);
		this.monthday = cal.get(Calendar.DAY_OF_MONTH);
		this.month = cal.get(Calendar.MONTH);
		this.year = cal.get(Calendar.YEAR);
	}

	/**
	 *  Create a new time.
	 *  @param seconds The seconds.
	 *  @param minutes The minutes.
	 *  @param hours The hours.
	 *  @param weekday The weekday.
	 *  @param monthday The day in month.
	 *  @param month The month.
	 *  @param year The year.
	 */
	public Time(int seconds, int minutes, int hours, int weekday,
		int monthday, int month, int year)
	{
		this.seconds = seconds;
		this.minutes = minutes;
		this.hours = hours;
		this.weekday = weekday;
		this.monthday = monthday;
		this.month = month;
		this.year = year;
	}

	//-------- methods --------

	/**
	 *  Get the seconds.
	 *  @return The seconds.
	 */
	public int getSeconds()
	{
		return seconds;
	}

	/**
	 *  Set the seconds.
	 *  @param seconds The seconds.
	 */
	public void setSeconds(int seconds)
	{
		this.seconds = seconds;
	}

	/**
	 *  Get the minutes.
	 *  @return The minutes.
	 */
	public int getMinutes()
	{
		return minutes;
	}

	/**
	 *  Set the minutes.
	 *  @param minutes The minutes.
	 */
	public void setMinutes(int minutes)
	{
		this.minutes = minutes;
	}

	/**
	 *  Get the hours.
	 *  @return The hours.
	 */
	public int getHours()
	{
		return hours;
	}

	/**
	 *  Set the hours.
	 *  @param hours The hours.
	 */
	public void setHours(int hours)
	{
		this.hours = hours;
	}

	/**
	 *  Get the weekday.
	 *  @return The weekday.
	 */
	public int getWeekday()
	{
		return weekday;
	}

	/**
	 *  Set the weekday.
	 *  @param weekday The weekday.
	 */
	public void setWeekday(int weekday)
	{
		this.weekday = weekday;
	}

	/**
	 *  Get the monthday.
	 *  @return The monthday.
	 */
	public int getMonthday()
	{
		return monthday;
	}

	/**
	 *  Set the monthday.
	 *  @param monthday The monthday.
	 */
	public void setMonthday(int monthday)
	{
		this.monthday = monthday;
	}

	/**
	 *  Get the month.
	 *  @return The month.
	 */
	public int getMonth()
	{
		return month;
	}

	/**
	 *  Set the month.
	 *  @param month The month.
	 */
	public void setMonth(int month)
	{
		this.month = month;
	}

	/**
	 *  Get the year.
	 *  @return The year.
	 */
	public int getYear()
	{
		return year;
	}

	/**
	 *  Set the year.
	 *  @param year The year.
	 */
	public void setYear(int year)
	{
		this.year = year;
	}

	//-------- methods --------

	/**
	 *  Test for equality.
	 *  @param o The object to test.
	 *  @return True if equal.
	 */
	public boolean equals(Object o)
	{
		if(this == o)
		{
			return true;
		}
		if(o == null || getClass() != o.getClass())
		{
			return false;
		}

		final Time time = (Time)o;

		if(hours != time.hours)
		{
			return false;
		}
		if(minutes != time.minutes)
		{
			return false;
		}
		if(month != time.month)
		{
			return false;
		}
		if(monthday != time.monthday)
		{
			return false;
		}
		if(seconds != time.seconds)
		{
			return false;
		}
		if(weekday != time.weekday)
		{
			return false;
		}
		if(year != time.year)
		{
			return false;
		}

		return true;
	}

	/**
	 *  Compute the hashcode.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		int result;
		result = seconds;
		result = 29 * result + minutes;
		result = 29 * result + hours;
		result = 29 * result + weekday;
		result = 29 * result + monthday;
		result = 29 * result + month;
		result = 29 * result + year;
		return result;
	}

	/**
	 * Creates and returns a copy of this object.  The precise meaning
	 * of "copy" may depend on the class of the object. The general
	 * intent is that, for any object <tt>x</tt>, the expression:
	 */
	public Object clone()
	{
		Time ret = null;
		try
		{
			ret = (Time)super.clone();
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}
}