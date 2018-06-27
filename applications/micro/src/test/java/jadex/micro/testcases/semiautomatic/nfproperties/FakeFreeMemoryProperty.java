package jadex.micro.testcases.semiautomatic.nfproperties;

import jadex.bridge.nonfunctional.AbstractNFProperty;
import jadex.bridge.nonfunctional.NFPropertyMetaInfo;
import jadex.bridge.sensor.unit.MemoryUnit;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Property simulating a random amount of free memory.
 *
 */
public class FakeFreeMemoryProperty extends AbstractNFProperty<Long, MemoryUnit>
{
	/**
	 * Creates the property.
	 */
	public FakeFreeMemoryProperty()
	{
		super(new NFPropertyMetaInfo("fakefreemem", Long.class, MemoryUnit.class, true, 3000, true, null));
	}
	
	/**
	 *  Returns the property value.
	 */
	public IFuture<Long> getValue(MemoryUnit unit)
	{
		long ret = (long) Math.round(Math.random() * 17179869184.0);
		if(unit!=null)
		{
			ret = unit.convert(ret);
		}
		
		return new Future<Long>(ret);
	}
}
