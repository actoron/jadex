package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;
import jadex.bridge.ITimedObject;

/**
 * 
 */
public class RoundBasedExecutor extends DeltaTimeExecutor
{
	//-------- constants --------
	
	/** The round time. */
	protected IVector1 roundtime;
	
	/** The elapsed time. */
	protected IVector1 elapsed;
	
	//-------- constructors--------
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public RoundBasedExecutor(IVector1 timecoefficient, IClockService clockservice, IVector1 roundtime)
	{
		super(timecoefficient, clockservice);
		this.roundtime = roundtime;
		this.elapsed = Vector1Double.ZERO;
	}
	
	//-------- ISpaceExecutor--------
	
	/**
	 * Sets the space for the executor. Called by the space when the executor is added.
	 * @param space the space being executed
	 */
	public void init(final IEnvironmentSpace space)
	{
		clockservice.createTimer(roundtime.getAsLong(), new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				IVector1 progress = timecoefficient.copy().multiply(new Vector1Long(currenttime - timestamp));
				timestamp = currenttime;
				
				space.step(progress);
				
				clockservice.createTimer(roundtime.getAsLong(), this);
			}
		});
	}
}
