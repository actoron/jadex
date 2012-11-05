package jadex.platform.service.cron;

import jadex.commons.IFilter;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *  Filter for testing if a time point matches one specific pattern.
 */
public class TimeFilter implements IFilter<Long>
{
	//-------- attributes --------
	
	/** The filters. */
	protected IFilter<Integer>[] filters;
	
	//-------- constructors --------
	
	/**
	 *  Create a new time filter.
	 *  @param filters The filters.
	 */
	public TimeFilter(IFilter<Integer>[] filters)
	{
		if(filters==null)
			System.out.println("here");
		this.filters = filters;
	}
	
	//-------- methods --------
	
	/**
	 *  Test if timepoint matches filter.
	 *  @param time The time.
	 *  @return True, if ok.
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