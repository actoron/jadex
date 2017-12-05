package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Property simulating a random amount of network bandwidth.
 *
 */
public class FakeNetworkBandwidthProperty extends AbstractNFProperty<Long, MemoryUnit>
{
	/** Persistent simulated bandwidth. */
	long fakenet = (long) Math.round(Math.random() * 1000.0);
	
	/**
	 * Creates the property.
	 */
	public FakeNetworkBandwidthProperty()
	{
		super(new NFPropertyMetaInfo("fakenetworkbandwith", Long.class, MemoryUnit.class, false, -1, false, null));
	}
	
	/**
	 *  Returns the property value.
	 */
	public IFuture<Long> getValue(MemoryUnit unit)
	{
		long ret = fakenet;
		if(unit!=null)
		{
			ret = unit.convert(ret);
		}
		
		return new Future<Long>(ret);
	}
}
