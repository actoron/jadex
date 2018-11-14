package jadex.platform.service.cron;

import java.util.Calendar;
import java.util.GregorianCalendar;

import jadex.commons.IFilter;

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
//		if(filters==null)
//			System.out.println("here");
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
		
		return filters[0].filter(Integer.valueOf(minute)) && filters[1].filter(Integer.valueOf(hour)) 
			&& filters[2].filter(Integer.valueOf(dom)) && filters[3].filter(Integer.valueOf(month)) 
			&& filters[4].filter(Integer.valueOf(dow));
	}

	/**
	 *  Get the filters.
	 *  return The filters.
	 */
	public IFilter<Integer>[] getFilters()
	{
		return filters;
	}

	/**
	 *  Set the filters. 
	 *  @param filters The filters to set.
	 */
	public void setFilters(IFilter<Integer>[] filters)
	{
		this.filters = filters;
	}
}