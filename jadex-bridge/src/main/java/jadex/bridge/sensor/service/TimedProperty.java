package jadex.bridge.sensor.service;

import jadex.bridge.IInternalAccess;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.nonfunctional.SimpleValueNFProperty;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
public abstract class TimedProperty extends SimpleValueNFProperty<Long, TimedProperty.TimeUnit>
{
	/** The allowed units. */
	public static enum TimeUnit{MILLIS, SECS, MINS, HOURS, DAYS}
	
	/**
	 *  Create a new property.
	 */
	public TimedProperty(String name, final IInternalAccess comp, long updaterate)
	{
		super(comp, new NFPropertyMetaInfo(name, long.class, TimeUnit.class, 
			updaterate>0? true: false, updaterate, null));
	}
	
	/**
	 *  Get the value.
	 */
	public IFuture<Long> getValue(TimeUnit unit)
	{
		long ret = value;
		
		if(unit!=null)
		{
			if(TimeUnit.SECS.equals(unit))
			{
				ret = Math.round(ret/1000d);
			}
			else if(TimeUnit.MINS.equals(unit))
			{
				ret = Math.round(ret/1000d/60);
			}
			else if(TimeUnit.HOURS.equals(unit))
			{
				ret = Math.round(ret/1000d/60/60);
			}
			else if(TimeUnit.DAYS.equals(unit))
			{
				ret = Math.round(ret/1000d/60/24);
			}
		}
		
		return new Future<Long>(ret);
	}
}
