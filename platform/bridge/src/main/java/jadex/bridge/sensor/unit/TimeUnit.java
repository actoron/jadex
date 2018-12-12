package jadex.bridge.sensor.unit;


/**
 *  The time unit.
 */
public enum TimeUnit implements IConvertableUnit<Long>
{
	MILLIS, SECS, MINS, HOURS, DAYS;
	
	/**
	 *  Convert to a known unit.
	 */
	public Long convert(Long value)
	{
		if(value!=null)
		{
			long ret = value;
			
			if(TimeUnit.SECS.equals(this))
			{
				ret = Math.round(ret/1000d);
			}
			else if(TimeUnit.MINS.equals(this))
			{
				ret = Math.round(ret/1000d/60);
			}
			else if(TimeUnit.HOURS.equals(this))
			{
				ret = Math.round(ret/1000d/60/60);
			}
			else if(TimeUnit.DAYS.equals(this))
			{
				ret = Math.round(ret/1000d/60/24);
			}
			return Long.valueOf(ret);
		}
		else
		{
			return null;
		}
	}
}
