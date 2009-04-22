package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;
import jadex.bridge.ITimedObject;

/**
 * 
 */
public class RoundBasedExecutor
{
	//-------- constants --------
	
	/** The time coefficient */
	protected IVector1 timecoefficient;
	
	/** Current time stamp */
	protected long timestamp;
	
	//-------- constructors--------
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public RoundBasedExecutor(final IEnvironmentSpace space, final IClockService clockservice, final IVector1 roundtime)
	{
		this(space, null, clockservice, roundtime);
	}
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public RoundBasedExecutor(final IEnvironmentSpace space, IVector1 timecoefficient, final IClockService clockservice, final IVector1 roundtime)
	{
		this.timecoefficient = timecoefficient==null? new Vector1Double(0.001): timecoefficient;
		this.timestamp = clockservice.getTime();
		
		clockservice.createTimer(roundtime.getAsLong(), new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
//				IVector1 progress = RoundBasedExecutor.this.timecoefficient.copy().multiply(new Vector1Long(currenttime - timestamp));
				IVector1 progress = new Vector1Long(currenttime - timestamp);
				timestamp = currenttime;
				
//				System.out.println("time: "+timestamp+" "+progress+" "+roundtime.getAsLong());
				
				space.step(progress);
				
				clockservice.createTimer(roundtime.getAsLong(), this);
			}
		});
	}
}
