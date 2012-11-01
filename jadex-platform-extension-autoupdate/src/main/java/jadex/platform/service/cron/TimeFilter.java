package jadex.platform.service.cron;

import jadex.commons.IFilter;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * 
 */
public class TimeFilter implements IFilter<Long>
{
	/** The filters. */
	protected IFilter<Integer>[] filters;
	
	/**
	 * 
	 */
	public TimeFilter(IFilter<Integer>[] filters)
	{
		this.filters = filters;
	}
	
	/**
	 * 
	 */
	public boolean filter(Long time)
	{
		GregorianCalendar gc = new GregorianCalendar();
		gc.setTimeInMillis(time.longValue());
//		gc.setTimeZone(timezone);
		int minute = gc.get(Calendar.MINUTE);
		int hour = gc.get(Calendar.HOUR_OF_DAY);
		int dom = gc.get(Calendar.DAY_OF_MONTH);
		int month = gc.get(Calendar.MONTH) + 1;
		int dow = gc.get(Calendar.DAY_OF_WEEK) - 1;
//		int year = gc.get(Calendar.YEAR);
	
		//(dom, month, gc.isLeapYear(year))
		
//		System.out.println("match: "+SUtil.SDF.format(time)+"  "+minute+" "+hour+" "+dom+" "+month+" "+dow);
		
		return filters[0].filter(new Integer(minute)) && filters[1].filter(new Integer(hour)) 
			&& filters[2].filter(new Integer(dom)) && filters[3].filter(new Integer(month)) 
			&& filters[4].filter(new Integer(dow));
	}
}