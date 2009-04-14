package jadex.adapter.base.envsupport.environment;

import java.util.Map;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Double;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Space executor that connects to a clock service and emits time deltas.
 */
public class DeltaTimeExecutor
{
	//-------- attributes --------
	
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
	public DeltaTimeExecutor(final IEnvironmentSpace space, IVector1 timecoefficient, final IClockService clockservice)
	{
		this.timecoefficient = timecoefficient==null? new Vector1Double(0.001): timecoefficient;
		this.timestamp = clockservice.getTime();
		
		clockservice.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{				
				long currenttime = clockservice.getTime();
				IVector1 progress = DeltaTimeExecutor.this.timecoefficient.copy().multiply(new Vector1Long(currenttime - timestamp));
				timestamp = currenttime;

				System.out.println("step: "+timestamp+" "+progress);
	
				space.step(progress);
			}
		});
	}
}
