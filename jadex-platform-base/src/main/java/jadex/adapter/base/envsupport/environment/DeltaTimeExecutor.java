package jadex.adapter.base.envsupport.environment;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;

/**
 * Space executor that connects to a clock service and emits time deltas.
 */
public class DeltaTimeExecutor implements ISpaceExecutor, ChangeListener
{
	/** The time coefficient */
	protected IVector1 timecoefficient;
	
	/** Current time stamp */
	protected long timestamp;
	
	/** The clock service */
	protected IClockService clockservice;
	
	/** The space */
	protected IEnvironmentSpace space;
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor(IVector1 timecoefficient, IClockService clockservice)
	{
		this.timecoefficient = timecoefficient==null? new Vector1Double(0.001): timecoefficient;
		this.clockservice = clockservice;
	}
	
	/**
	 * Sets the space for the executor. Called by the space when the executor is added.
	 * @param space the space being executed
	 */
	public void setSpace(IEnvironmentSpace space)
	{
		this.space = space;
		timestamp = clockservice.getTime();
		clockservice.addChangeListener(this);
	}
	
	public void stateChanged(ChangeEvent e)
	{
		long currenttime = clockservice.getTime();
		IVector1 progress = timecoefficient.copy().multiply(new Vector1Long(currenttime - timestamp));
		timestamp = currenttime;
		
		space.step(progress);
	}

}
