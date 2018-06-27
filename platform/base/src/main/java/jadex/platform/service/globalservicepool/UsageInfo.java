package jadex.platform.service.globalservicepool;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Information about service usage.
 */
public class UsageInfo 
{
	//-------- attributes --------
	
	/** The service id. */
	protected IServiceIdentifier sid;

//	/** The considered time interval (in millis). */
//	protected long timeinterval;
	
	/** The start time. */
	protected long starttime;
	
	/** The number of usages in the last time interval. */
	protected double usages;
	
//	/** The average service time in the last time interval. */
//	protected double servicetime;

	//-------- methods --------
	
	/**
	 *  Create a new UsageInfo.
	 */
	public UsageInfo() 
	{
	}
	
	/**
	 *  Create a new UsageInfo.
	 */
	public UsageInfo(IServiceIdentifier sid, long starttime, double usages)//, double servicetime) 
	{
		this.sid = sid;
		this.starttime = starttime;
		this.usages = usages;
//		this.servicetime = servicetime;
	}

	/**
	 *  Get the serviceIdentifier.
	 *  @return the serviceIdentifier
	 */
	public IServiceIdentifier getServiceIdentifier() 
	{
		return sid;
	}
	
	/**
	 *  Get the startTime.
	 *  @return the startTime
	 */
	public long getStartTime() 
	{
		return starttime;
	}

	/**
	 *  Set the startTime.
	 *  @param startTime The startTime to set
	 */
	public void setStartTime(long startTime) 
	{
		this.starttime = startTime;
	}

	/**
	 *  Set the serviceIdentifier.
	 *  @param sid The serviceIdentifier to set
	 */
	public void setServiceIdentifier(IServiceIdentifier sid) 
	{
		this.sid = sid;
	}

//	/**
//	 *  Get the timeInterval.
//	 *  @return the timeInterval
//	 */
//	public long getTimeInterval() 
//	{
//		return timeinterval;
//	}
//
//	/**
//	 *  Set the timeInterval.
//	 *  @param timeInterval The timeInterval to set
//	 */
//	public void setTimeInterval(long timeInterval) 
//	{
//		this.timeinterval = timeInterval;
//	}

	/**
	 *  Get the usages.
	 *  @return the usages
	 */
	public double getUsages() 
	{
		return usages;
	}

	/**
	 *  Set the usages.
	 *  @param usages The usages to set
	 */
	public void setUsages(double usages) 
	{
		this.usages = usages;
	}

//	/**
//	 *  Get the meanServiceTime.
//	 *  @return the meanServiceTime
//	 */
//	public double getMeanServiceTime() 
//	{
//		return servicetime;
//	}
//
//	/**
//	 *  Set the meanServiceTime.
//	 *  @param meanServiceTime The meanServiceTime to set
//	 */
//	public void setMeanServiceTime(double meanServiceTime) 
//	{
//		this.servicetime = meanServiceTime;
//	}
	
	/**
	 *  Integrate a new usage info.
	 *  Uses ema to average the values.
	 */
	public void integrateUsage(UsageInfo ui)
	{
//		setMeanServiceTime(calculateEma(ui.getMeanServiceTime(), getMeanServiceTime()));
		setUsages(calculateEma(ui.getUsages(), getUsages()));

		if(ui.getStartTime()>starttime)
			setStartTime(ui.getStartTime());
	}
	
	/**
	 *  Calculate the exponential moving average.
	 */
	protected double calculateEma(double newval, double val)
	{
		// ema calculatio: EMAt = EMAt-1 +(SF*(Ct-EMAt-1)) SF=2/(n+1)
		double sf = 2d/(10d+1); // 10 periods per default
		double delta = newval-val;
		double ret = Long.valueOf((long)(val+sf*delta));
		return ret;
	}
}
